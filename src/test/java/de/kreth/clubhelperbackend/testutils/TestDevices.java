package de.kreth.clubhelperbackend.testutils;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;

public enum TestDevices implements Device {
	MOBILE, NORMAL, TABLET;

	@Override
	public DevicePlatform getDevicePlatform() {
		return equals(NORMAL) ? DevicePlatform.UNKNOWN : DevicePlatform.ANDROID;
	}

	@Override
	public boolean isMobile() {
		return equals(NORMAL) == false;
	}

	@Override
	public boolean isNormal() {
		return equals(NORMAL);
	}

	@Override
	public boolean isTablet() {
		return equals(TABLET);
	}

}
