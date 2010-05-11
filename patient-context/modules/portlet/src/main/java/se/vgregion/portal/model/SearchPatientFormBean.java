package se.vgregion.portal.model;

/**
 * This action do that and that, if it has something special it is.
 *
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class SearchPatientFormBean {

    private String searchText;

    private String historySearchText;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getHistorySearchText() {
        return historySearchText;
    }

    public void setHistorySearchText(String historySearchText) {
        this.historySearchText = historySearchText;
    }
}
