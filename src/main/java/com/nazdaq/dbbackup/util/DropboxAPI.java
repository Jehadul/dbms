package com.nazdaq.dbbackup.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

public class DropboxAPI {

	DbxClientV2 client = null;

	@SuppressWarnings("deprecation")
	public boolean uploadDropbox(String ACCESS_TOKEN, String filePath,
			String fileName, String uploadLocation, String dbname, String ip)
			throws DbxException, IOException, ParseException {

		boolean response = false;
		// Create Dropbox client
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial",
				"en_US");
		client = new DbxClientV2(config, ACCESS_TOKEN);

		// Get current account info
		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName());

		List<String> fileNames = new ArrayList<String>();
		boolean isExistFolder = this.existingFolderInDropbox(uploadLocation
				+ ip + "/" + dbname);
		if (isExistFolder) {
			ListFolderResult result = client.files().listFolder(
					uploadLocation + ip + "/" + dbname);
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					System.out.println(metadata.getPathLower());
					fileNames.add(metadata.getName());
				}

				if (!result.getHasMore()) {
					break;
				}

				result = client.files().listFolderContinue(result.getCursor());
			}
		}

		Map<Long, String> map = new TreeMap<Long, String>();
		List<Long> keys = new ArrayList<Long>();
		if (fileNames.size() > 9) {
			for (String fn : fileNames) {
				Long key = this.getKey(fn);
				map.put(key, fn);
				keys.add(key);

			}

			Collections.sort(keys);
			// delete first file(s) if more than 9 file
			String removableFileName = map.get(keys.get(0));

			this.deleteFile(uploadLocation + ip + "/" + dbname + "/"
					+ removableFileName);
		}

		// Upload to Dropbox
		try (InputStream in = new FileInputStream(filePath + fileName)) {
			FileMetadata metadata = client
					.files()
					.uploadBuilder(
							uploadLocation + ip + "/" + dbname + "/" + fileName)
					.uploadAndFinish(in);
			response = true;

			System.out.println(metadata.getPathLower());
		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		}

		return response;
	}

	public void deleteFile(String path) {
		try {
			@SuppressWarnings("unused")
			Metadata metadata = client.files().delete(path);
		} catch (DbxException dbxe) {
			dbxe.printStackTrace();
		}
	}

	public Long getKey(String name) throws ParseException {
		// TODO Auto-generated method stub
		String formatedName = name.substring(name.length() - 23,
				name.length() - 4);
		formatedName = formatedName.replaceAll("_", "-");

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(formatedName));

		Integer year = cal.get(Calendar.YEAR);
		Integer mon = cal.get(Calendar.MONTH) + 1;
		Integer day = cal.get(Calendar.DAY_OF_MONTH);
		Integer hours = cal.get(Calendar.HOUR_OF_DAY);
		Integer minute = cal.get(Calendar.MINUTE);
		Integer second = cal.get(Calendar.SECOND);

		/*String d = "" + day;
		if ((day.toString()).trim().length() < 2) {
			d = "0" + day;
		}*/

		/*String m = "" + mon;
		if ((mon.toString()).trim().length() < 2) {
			m = "0" + mon;
		}*/
		
		String d = (day.toString()).trim().length() < 2 ? "0" + day : day + "";
		String m = (mon.toString()).trim().length() < 2 ? "0" + mon : mon + "";

		String h = (hours.toString()).trim().length() < 2 ? "0" + hours : hours + "";
		String min = (minute.toString()).trim().length() < 2 ? "0" + minute : minute + "";
		String sec = (second.toString()).trim().length() < 2 ? "0" + second : second + "";

		String key = year + m + d + h + min + sec;

		return Long.parseLong(key);

	}

	public boolean existingFolderInDropbox(String filepath) throws DbxException {

		boolean result = false;
		try {
			client.files().getMetadata(filepath);
			result = true;
		} catch (GetMetadataErrorException e) {
			result = false;
			// TODO Auto-generated catch block
			if (e.errorValue.isPath()) {
				result = false;
			}
		}
		return result;
	}

}
