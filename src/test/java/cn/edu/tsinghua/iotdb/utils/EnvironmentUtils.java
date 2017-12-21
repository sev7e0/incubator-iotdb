package cn.edu.tsinghua.iotdb.utils;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.edu.tsinghua.iotdb.auth.dao.Authorizer;
import cn.edu.tsinghua.iotdb.conf.TsfileDBConfig;
import cn.edu.tsinghua.iotdb.conf.TsfileDBDescriptor;
import cn.edu.tsinghua.iotdb.engine.cache.RowGroupBlockMetaDataCache;
import cn.edu.tsinghua.iotdb.engine.cache.TsFileMetaDataCache;
import cn.edu.tsinghua.iotdb.engine.filenode.FileNodeManager;
import cn.edu.tsinghua.iotdb.exception.FileNodeManagerException;
import cn.edu.tsinghua.iotdb.metadata.MManager;
import cn.edu.tsinghua.iotdb.sys.writelog.WriteLogManager;
import cn.edu.tsinghua.tsfile.common.conf.TSFileConfig;
import cn.edu.tsinghua.tsfile.common.conf.TSFileDescriptor;

/**
 * <p>
 * This class is used for cleaning test environment in unit test and integration
 * test
 * </p>
 * 
 * @author liukun
 *
 */
public class EnvironmentUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentUtils.class);

	private static TsfileDBConfig config = TsfileDBDescriptor.getInstance().getConfig();
	private static TSFileConfig tsFileConfig = TSFileDescriptor.getInstance().getConfig();

	public static void cleanEnv() throws IOException {
		//tsFileConfig.duplicateIncompletedPage = false;
		// clean filenode manager
		try {
			if (!FileNodeManager.getInstance().closeAll()) {
				LOGGER.error("Can't close the filenode manager");
				System.exit(1);
			}
		} catch (FileNodeManagerException e) {
			throw new IOException(e.getMessage());
		}
		FileNodeManager.getInstance().reset();
		// clean wal
		WriteLogManager.getInstance().close();
		// clean cache
		TsFileMetaDataCache.getInstance().clear();
		RowGroupBlockMetaDataCache.getInstance().clear();
		// close metadata
		MManager.getInstance().clear();
		MManager.getInstance().flushObjectToFile();
		// delete all directory
		cleanAllDir();
		//FileNodeManager.getInstance().reset();
	}

	private static void cleanAllDir() throws IOException {
		// delete bufferwrite
		cleanDir(config.bufferWriteDir);
		// delete overflow
		cleanDir(config.overflowDataDir);
		// delete filenode
		cleanDir(config.fileNodeDir);
		// delete metadata
		cleanDir(config.metadataDir);
		// delete wal
		cleanDir(config.walFolder);
		// delete derby
		cleanDir(config.derbyHome);
		// delete index
		cleanDir(config.indexFileDir);
		// delte data
		cleanDir("data");
		// delte derby log
		//cleanDir("derby.log");
	}

	public static void cleanDir(String dir) throws IOException {
		File file = new File(dir);
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File subFile : file.listFiles()) {
					cleanDir(subFile.getAbsolutePath());
				}
			}
			if (!file.delete()) {
				throw new IOException(String.format("The file %s can't be deleted", dir));
			}
		}
	}
	
	public static void envSetUp(){
		Authorizer.reset();
		//tsFileConfig.duplicateIncompletedPage = true;
		FileNodeManager.getInstance().reset();
	}
}