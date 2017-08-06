/*
 * Copyright (c) 2017.  TestVagrant Technologies
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.testvagrant.devicemanagement.utils;


import com.testvagrant.commons.entities.device.OSVersion;
import com.testvagrant.commons.entities.device.Platform;
import com.testvagrant.mdb.enums.AOSVersion;
import com.testvagrant.mdb.enums.IOSVersion;

import java.util.Arrays;

public class DeviceDetailsHelper {

    public OSVersion getOSVersion(String osVersion, Platform platform) {
        OSVersion ver = null;
        switch (platform){
            case ANDROID:
               ver = Arrays.stream(AOSVersion.values()).filter(version ->{
                   version.setVersion(osVersion);
                   return version.getVersion().equals(osVersion);
               }).findFirst().get();
                break;
            case IOS:
                ver = Arrays.stream(IOSVersion.values()).filter(version ->{
                    version.setVersion(osVersion);
                    return  version.getVersion().equals(osVersion);
                }).findFirst().get();
                break;
        }
        return ver;
    }
}
