package com.hapla.schedule.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Detail {
	private int datailNo;
	private int tripNo;
	private String content;
	private String apiId;
	private String type;
	private Date tripDate;

	
}
