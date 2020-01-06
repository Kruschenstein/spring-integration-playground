package org.grorg.integration.model.api;

public class User {
    private String _id;
    private Name name;
    private int upvotes;
    private int useUpvoted;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getUseUpvoted() {
        return useUpvoted;
    }

    public void setUseUpvoted(int useUpvoted) {
        this.useUpvoted = useUpvoted;
    }
}
