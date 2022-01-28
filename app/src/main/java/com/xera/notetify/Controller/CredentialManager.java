package com.xera.notetify.Controller;

import android.content.Context;

import com.huawei.cloud.base.auth.DriveCredential;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.client.exception.DriveCode;

    /**
     * Credential management class.
     * <p>
     * since 1.0
     */
    public class CredentialManager {

        /**
         * Drive authentication information.
         */
        private DriveCredential mCredential;

        private CredentialManager() {
        }

        private static class InnerHolder {
            private static CredentialManager sInstance = new CredentialManager();
        }

        /**
         * Singleton of CredentialManager.
         *
         * @return CredentialManager
         */
        public static CredentialManager getInstance() {
            return InnerHolder.sInstance;
        }

        /**
         * Initialize Drive based on the context and HUAWEI ID information including unionId, countrycode, and accessToken.
         * When the current accessToken expires, register an AccessMethod and obtain a new accessToken. The value of refreshAT cannot be null.
         *
         * @param unionID UnionID in HwID.
         * @param at        accessToken
         * @param refreshAT Update the accessToken callback function, without carrying null values.
         */
        public int init(String unionID, String at, DriveCredential.AccessMethod refreshAT) {
            if (StringUtils.isNullOrEmpty(unionID) || StringUtils.isNullOrEmpty(at)) {
                return DriveCode.ERROR;
            }
            DriveCredential.Builder builder = new DriveCredential.Builder(unionID, refreshAT);
            mCredential = builder.build().setAccessToken(at);
            return DriveCode.SUCCESS;
        }

        /**
         * Obtain DriveCredential.
         *
         * @return DriveCredential
         */
        public DriveCredential getCredential() {
            return mCredential;
        }

        /**
         * Exit Drive and clear all cache information generated during use of Drive.
         */
        public void exit(Context context) {
            // Delete cache files.
            deleteFile(context.getCacheDir());
            deleteFile(context.getFilesDir());
        }

        /**
         * Delete cache files.
         *
         * @param file Designated cache file.
         */
        private static void deleteFile(java.io.File file) {
            if (null == file || !file.exists()) {
                return;
            }

            if (file.isDirectory()) {
                java.io.File[] files = file.listFiles();
                if (files != null) {
                    for (java.io.File f : files) {
                        deleteFile(f);
                    }
                }
            }
        }
    }

