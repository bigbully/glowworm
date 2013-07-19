package com.jd.bdp.seomonitor.model.page;

import java.util.Date;


public class SeoWord{

	private Long id;


	private Date grabDate;

	private String ranking;

	private Long searchNumber;

	private String url;

	private Integer isChange;
	private String changeDesc;

	private Long wordConfigId;
	private String wordName;
    private String person;

	private Date operateTime;
    private String searchTime;
    private Integer start;
    private Integer limit;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }


    // ====================================================================


    public String getRanking() {
        return ranking;
    }

    public void setRanking() {
        this.ranking = ranking;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }


    public String getChangeDesc() {
//        if(isChange != null) {
//            changeDesc = ChangeEnum.enumValueOf(isChange).toName();
//        }
        return changeDesc;
    }

    public void setChangeDesc(String changeDesc) {
        this.changeDesc = changeDesc;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public Integer getChange() {
        return isChange;
    }

    public void setChange(Integer change) {
        isChange = change;
    }

    public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getGrabDate() {
		return grabDate;
	}

	public void setGrabDate(Date grabDate) {
		this.grabDate = grabDate;
	}

	public Long getSearchNumber() {
		return searchNumber;
	}

	public void setSearchNumber(Long searchNumber) {
		this.searchNumber = searchNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}



	public Long getWordConfigId() {
		return wordConfigId;
	}

	public void setWordConfigId(Long wordConfigId) {
		this.wordConfigId = wordConfigId;
	}

}
