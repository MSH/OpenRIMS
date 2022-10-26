package org.msh.pharmadex2.dto.metric;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * One time to reply record
 * @author alexk
 *
 */
public class TimeToReplyDTO {
	private LocalDateTime minute = LocalDateTime.now();
	private AtomicInteger quantity= new AtomicInteger(0);
	private AtomicInteger durationMils = new AtomicInteger(0);
	private AtomicInteger maxMils = new AtomicInteger(0);
	private AtomicLong durationMilsTotal = new AtomicLong(0);
	private AtomicLong quantityTotal = new AtomicLong(0l);
	public LocalDateTime getMinute() {
		return minute;
	}
	public void setMinute(LocalDateTime minute) {
		this.minute = minute;
	}
	public AtomicInteger getQuantity() {
		return quantity;
	}
	public void setQuantity(AtomicInteger quantity) {
		this.quantity = quantity;
	}
	public AtomicInteger getDurationMils() {
		return durationMils;
	}
	public void setDurationMils(AtomicInteger durationMils) {
		this.durationMils = durationMils;
	}
	public AtomicInteger getMaxMils() {
		return maxMils;
	}
	public void setMaxMils(AtomicInteger maxMils) {
		this.maxMils = maxMils;
	}
	public AtomicLong getDurationMilsTotal() {
		return durationMilsTotal;
	}
	public void setDurationMilsTotal(AtomicLong durationMilsTotal) {
		this.durationMilsTotal = durationMilsTotal;
	}
	public AtomicLong getQuantityTotal() {
		return quantityTotal;
	}
	public void setQuantityTotal(AtomicLong quantityTotal) {
		this.quantityTotal = quantityTotal;
	}
	@Override
	public String toString() {
		return "TimeToReplyDTO [minute=" + minute + ", quantity=" + quantity + ", durationMils=" + durationMils
				+ ", maxMils=" + maxMils + ", durationMilsTotal=" + durationMilsTotal + ", quantityTotal="
				+ quantityTotal + "]";
	}
	
}
