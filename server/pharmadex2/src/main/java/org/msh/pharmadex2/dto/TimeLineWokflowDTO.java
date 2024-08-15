package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TimeLineWokflowDTO {
	private long permitdataid=0l;		
	private long historyId=0l;
	private List<PointTimeLine> point=new ArrayList<PointTimeLine>();

	public long getHistoryId() {
		return historyId;
	}

	public List<PointTimeLine> getPoint() {
		return point;
	}

	public void setPoint(List<PointTimeLine> point) {
		this.point = point;
	}

	public long getPermitdataid() {
		return permitdataid;
	}

	public void setPermitdataid(long permitdataid) {
		this.permitdataid = permitdataid;
	}
	
}
