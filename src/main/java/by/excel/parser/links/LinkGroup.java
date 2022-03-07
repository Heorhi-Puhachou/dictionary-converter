package by.excel.parser.links;

import java.util.ArrayList;

public class LinkGroup {

    private String groupName;
    private ArrayList<Link> links;

    public LinkGroup(String groupName, ArrayList<Link> links) {
        this.groupName = groupName;
        this.links = links;
    }


    public String getGroupName() {
        return groupName;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }


}
