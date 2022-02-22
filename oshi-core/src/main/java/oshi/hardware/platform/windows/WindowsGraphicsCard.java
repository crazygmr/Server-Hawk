/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oshi.hardware.platform.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.VersionHelpers; // NOSONAR squid:S1191
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.OhmHardware;
import oshi.driver.windows.wmi.OhmSensor;
import oshi.driver.windows.wmi.Win32VideoController;
import oshi.driver.windows.wmi.Win32VideoController.VideoControllerProperty;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.Constants;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

/**
 * Graphics Card obtained from WMI
 */
@Immutable
final class WindowsGraphicsCard extends AbstractGraphicsCard {

    private static final Logger LOG = LoggerFactory.getLogger(WindowsGraphicsCard.class);

    private static final String COM_EXCEPTION_MSG = "COM exception: {}";

    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();

    /**
     * Constructor for WindowsGraphicsCard
     *
     * @param name
     *            The name
     * @param deviceId
     *            The device ID
     * @param vendor
     *            The vendor
     * @param versionInfo
     *            The version info
     * @param vram
     *            The VRAM
     */
    WindowsGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
        super(name, deviceId, vendor, versionInfo, vram);
    }

    /**
     * public method used by
     * {@link oshi.hardware.common.AbstractHardwareAbstractionLayer} to access the
     * graphics cards.
     *
     * @return List of {@link oshi.hardware.platform.windows.WindowsGraphicsCard}
     *         objects.
     */
    public static List<GraphicsCard> getGraphicsCards() {
        List<GraphicsCard> cardList = new ArrayList<>();
        if (IS_VISTA_OR_GREATER) {
            WmiResult<VideoControllerProperty> cards = Win32VideoController.queryVideoController();
            for (int index = 0; index < cards.getResultCount(); index++) {
                String name = WmiUtil.getString(cards, VideoControllerProperty.NAME, index);
                Triplet<String, String, String> idPair = ParseUtil.parseDeviceIdToVendorProductSerial(
                        WmiUtil.getString(cards, VideoControllerProperty.PNPDEVICEID, index));
                String deviceId = idPair == null ? Constants.UNKNOWN : idPair.getB();
                String vendor = WmiUtil.getString(cards, VideoControllerProperty.ADAPTERCOMPATIBILITY, index);
                if (idPair != null) {
                    if (Util.isBlank(vendor)) {
                        deviceId = idPair.getA();
                    } else {
                        vendor = vendor + " (" + idPair.getA() + ")";
                    }
                }
                String versionInfo = WmiUtil.getString(cards, VideoControllerProperty.DRIVERVERSION, index);
                if (Util.isBlank(versionInfo)) {
                    versionInfo = Constants.UNKNOWN;
                }
                long vram = WmiUtil.getUint32asLong(cards, VideoControllerProperty.ADAPTERRAM, index);
                cardList.add(new WindowsGraphicsCard(Util.isBlank(name) ? Constants.UNKNOWN : name, deviceId,
                        Util.isBlank(vendor) ? Constants.UNKNOWN : vendor, versionInfo, vram));
            }
        }
        return cardList;
    }

    @Override
    public float queryMaxFreq() {
        return getMaxFreqFromOHM();
    }

    public static float getMaxFreqFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Clock speed data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "Clock");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.MAX, 0);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }

    @Override
    public float queryCurrentFreq() {
        return getCurrentFreqFromOHM();
    }

    public static float getCurrentFreqFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Clock speed data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "Clock");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, 0);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }

    @Override
    public float queryTemperature() {
        return getTempFromOHM();
    }

    public static float getTempFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Temperature data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "Temperature");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, 0);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }

    @Override
    public float queryUtilization() {
        return getUtilizationFromOHM();
    }

    public static float getUtilizationFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Utilization data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "Load");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE, 1);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }

    @Override
    public float queryFreeMem() {
        return getFreeMemFromOHM();
    }

    public static float getFreeMemFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Utilization data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "SmallData");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE,1);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }

    @Override
    public float queryUsedMem() {
        return getUsedMemFromOHM();
    }

    public static float getUsedMemFromOHM() {
        WmiQueryHandler h = Objects.requireNonNull(WmiQueryHandler.createInstance());
        boolean comInit = false;
        try {
            comInit = h.initCOM();
            //WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "Gpuati");
            WmiResult<OhmHardware.IdentifierProperty> ohmHardware = OhmHardware.queryHwIdentifier(h, "Hardware", "GpuNvidia");
            if (ohmHardware.getResultCount() > 0) {
                LOG.debug("Found Utilization data in Open Hardware Monitor");
                // Look for identifier containing "gpu"
                String gpuIdentifier = null;
                for (int i = 0; i < ohmHardware.getResultCount(); i++) {
                    String id = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, i);
                    if (id.toLowerCase().contains("gpu")) {
                        gpuIdentifier = id;
                        break;
                    }
                }
                // If none found, just get the first one
                if (gpuIdentifier == null) {
                    gpuIdentifier = WmiUtil.getString(ohmHardware, OhmHardware.IdentifierProperty.IDENTIFIER, 1);
                }
                // Now fetch sensor
                WmiResult<OhmSensor.ValueProperty> ohmSensors = OhmSensor.querySensorValue(h, gpuIdentifier, "SmallData");
                if (ohmSensors.getResultCount() > 0) {
                    return WmiUtil.getFloat(ohmSensors, OhmSensor.ValueProperty.VALUE,2);
                }
            }
        } catch (COMException e) {
            LOG.warn(COM_EXCEPTION_MSG, e.getMessage());
        } finally {
            if (comInit) {
                h.unInitCOM();
            }
        }
        return 0;
    }
}
