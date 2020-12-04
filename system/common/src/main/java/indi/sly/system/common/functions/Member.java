package indi.sly.system.common.functions;

public class Member<T> {
    T member;

    public T getMember() {
        return this.member;
    }

    public void setMember(T member) {
        this.member = member;
    }
}
