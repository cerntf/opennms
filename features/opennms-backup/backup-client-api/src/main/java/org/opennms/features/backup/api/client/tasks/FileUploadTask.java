/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
package org.opennms.features.backup.api.client.tasks;

import org.opennms.features.backup.api.client.BackupService;
import org.opennms.features.backup.api.helpers.UploadHelper;

import java.util.Map;
import java.util.TreeMap;

/**
 * This task is used to upload the full/diff backup file to a remote location.
 *
 * @author Christian Pape
 */
public class FileUploadTask extends AbstractBackupTask {
    /**
     * Constructor for instantiating new objects of this class.
     *
     * @param backupService the {@link BackupService} to be used
     */

    public FileUploadTask(BackupService backupService) {
        super(backupService);
    }

    @Override
    public String getName() {
        return "File upload";
    }

    @Override
    public void execute(long timestamp) throws Exception {
        /**
         * Construct the filename of the full/diff backup file created
         */
        String originalSnapshotFilename = getBackupService().getBackupConfig().getLocalDirectory() + "/backup." + timestamp + ".zip";

        /**
         * Setting some additional parameters for the file upload
         */
        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("timestamp", String.valueOf(timestamp));
        parameters.put("systemId", getBackupService().getBackupConfig().getSystemId());
        parameters.put("customerId", getBackupService().getBackupConfig().getCustomerId());

        /**
         * Upload the file to the remote location
         */
        UploadHelper.upload(getBackupService().getBackupConfig().getBackupUrl(), getBackupService().getBackupConfig().getUsername(), getBackupService().getBackupConfig().getPassword(), originalSnapshotFilename, parameters);
    }

    @Override
    public void rollback(long timestamp) {
        /**
         * This task has not created any files, so do nothing
         */
    }
}