package indi.sly.system.common.lang;

public class Member1<T> {
    @SuppressWarnings("NullAway.Init")
    T member;

    public T getMember() {
        return this.member;
    }

    public void setMember(T member) {
        this.member = member;
    }
}
