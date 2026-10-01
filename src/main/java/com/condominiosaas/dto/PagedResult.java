package com.condominiosaas.dto;

import java.util.List;

public class PagedResult<T> {
	private List<T> items;
	private long totalCount;
	private int pageIndex;
	private int linesPerPage;

	public PagedResult() {}

	public PagedResult(List<T> items, long totalCount, int pageIndex, int linesPerPage) {
		this.items = items;
		this.totalCount = totalCount;
		this.pageIndex = pageIndex;
		this.linesPerPage = linesPerPage;
	}

	public List<T> getItems() { return items; }
	public void setItems(List<T> items) { this.items = items; }

	public long getTotalCount() { return totalCount; }
	public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

	public int getPageIndex() { return pageIndex; }
	public void setPageIndex(int pageIndex) { this.pageIndex = pageIndex; }

	public int getLinesPerPage() { return linesPerPage; }
	public void setLinesPerPage(int linesPerPage) { this.linesPerPage = linesPerPage; }
}
