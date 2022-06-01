package com.chrisreal.useraccesslog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chrisreal.useraccesslog.entity.BlockedIpTable;
import com.chrisreal.useraccesslog.entity.UserAccessLog;
import com.chrisreal.useraccesslog.repository.BlockedIpRepository;
import com.chrisreal.useraccesslog.repository.UserAccessLogRepository;
import com.chrisreal.useraccesslog.utils.Utility;
import com.chrisreal.useraccesslog.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccessLogService {
	
	@Autowired
	public UserAccessLogRepository userAccessLogRepository;
	@Autowired
	public BlockedIpRepository blockedIpRepository;

	public void execute(String[] args) throws FileNotFoundException {
		// validate and extract args
		HashMap<String, String> arguments = validateAndExtractArgs(args);
		if (arguments.isEmpty()) {
			log.error("No arguments found, process is terminated!");
			throw new RuntimeException("No arguments found, process is terminated!");
		}

		Path assessFilePath = Paths.get(arguments.get(Constants.REQUIRED_ARGS[0]));
		String startDate = arguments.get(Constants.REQUIRED_ARGS[1]);
		String duration = arguments.get(Constants.REQUIRED_ARGS[2]);
		int limit = Integer.valueOf(arguments.get(Constants.REQUIRED_ARGS[3]));
		loadFileToDb(assessFilePath);
		
		List<String> blockedIPs = checkLimitAndBlockIPs(startDate, limit, duration);
		
		System.out.println("Blocked IPs: "+ blockedIPs);
		
		log.info("Execution completed");

	}

	public HashMap<String, String> validateAndExtractArgs(String[] args) {
		if (args == null || args.length < 0) {
			throw new RuntimeException("Please provide necessary arguments: accessFile, start, duration, limit");
		}

		HashMap<String, String> argMap = new HashMap<>();

		for (String arg : args) {
			String[] splitedString = arg.split("=");
			String key = splitedString[0].substring(2);
			// check if key is valid
			if (!Arrays.asList(Constants.REQUIRED_ARGS).contains(key)) {
				log.error("The argument " + key + " you provided is invalid");
				throw new RuntimeException("The argument " + key + " you provided is invalid");
			}

			String value = splitedString[1];
			argMap.put(key, value);
		}

		return argMap;

	}
	
	public boolean loadFileToDb(Path filePath) throws FileNotFoundException {
		List<UserAccessLog> userAccessLogs = extractFileContents(filePath);
		
		if(userAccessLogs.isEmpty()) {
			log.error("Could not extract user access log data from file");
			return false;
		}
		
		userAccessLogRepository.saveAll(userAccessLogs);
		
		log.info("Saving data to user access log completed!");
		
		return true;
    }
	
	@SuppressWarnings("unused")
	public List<UserAccessLog> extractFileContents(Path filePath) throws FileNotFoundException {
		File file = new File(String.valueOf(filePath));
        Scanner fileReader = new Scanner(file);
        List<UserAccessLog> userAccessLogs = new ArrayList<>();
        while (fileReader.hasNextLine()) {
            String data = fileReader.nextLine();
            String sp = data.replace("|", ",");
            String[] splited = sp.split(",");
            String date = splited[0].substring(0, 19);
            String ip = splited[1];
            String request = splited[2];
            String status = splited[3];
            String userAgent = splited[4];
            UserAccessLog userAccessLog = setObject(date, ip, request, status, userAgent);

            userAccessLogs.add(userAccessLog);
        }

        fileReader.close();
        
        return userAccessLogs;
    }
	
	private UserAccessLog setObject(String date, String ip, String request, String status, String userAgent) {
        UserAccessLog userAccessLog = new UserAccessLog();
        LocalDateTime localDateTime = LocalDateTime.parse((date), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        userAccessLog.setDate(localDateTime);
        userAccessLog.setIp(ip);
        userAccessLog.setRequest(request);
        userAccessLog.setStatus(status);
        userAccessLog.setUserAgent(userAgent);
        
        return userAccessLog;
    }

	@SuppressWarnings("static-access")
	public List<String> checkLimitAndBlockIPs(String start, int limit, String duration) {
		String startDate = Utility.formatStartDateTime(start);
		LocalDateTime startDateTime = LocalDateTime.parse(startDate,
				DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
		LocalDateTime endDateTime = Utility.getEndDateTime(startDate, duration);
		int durationAllocated = Utility.getDurationInHours(duration);
		
		List<String> listOfIpsToBlock = userAccessLogRepository
				.getAllIpByDateRange(startDateTime, endDateTime, limit);
		
		saveBlockedIpAddress(limit, durationAllocated, startDateTime, endDateTime, listOfIpsToBlock);
		
		return listOfIpsToBlock;
	}

	private void saveBlockedIpAddress(int limit, int durationAllocated, LocalDateTime startDateTime,
			LocalDateTime endDateTime, List<String> listOfIpsToBlock) {
		
		listOfIpsToBlock.stream().forEach(ip -> {
			BlockedIpTable blockedIpTable = new BlockedIpTable();
			blockedIpTable.setIp(ip);
			blockedIpTable.setComment("Request during the duration is greater than limit of " + limit);
			int count = userAccessLogRepository.findCountByIp(startDateTime, endDateTime, ip);
			blockedIpTable.setRequestNumber(count);
			
			log.warn(
					"This IP {} has been blocked for making {} requests more than the set limit of {} within this duration {}",
					ip, String.valueOf(count), String.valueOf(limit), String.valueOf(durationAllocated));
			
			blockedIpRepository.save(blockedIpTable);
		});
	}
}
