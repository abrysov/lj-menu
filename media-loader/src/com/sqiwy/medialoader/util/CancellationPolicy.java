package com.sqiwy.medialoader.util;

/**
 * Created by abrysov
 */

public class CancellationPolicy {
	private boolean isCancelled;

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}