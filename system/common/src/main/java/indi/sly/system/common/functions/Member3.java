package indi.sly.system.common.functions;

public class Member3<T1, T2, T3> {
    T1 member1;
    T2 member2;
    T3 member3;

    public T1 getMember1() {
        return this.member1;
    }

    public void setMember1(T1 member) {
        this.member1 = member;
    }

    public T2 getMember2() {
        return this.member2;
    }

    public void setMember2(T2 member) {
        this.member2 = member;
    }

    public T3 getMember3() {
        return this.member3;
    }

    public void setMember3(T3 member) {
        this.member3 = member;
    }
}