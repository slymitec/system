package indi.sly.system.common.functions;

public class Member2<T1, T2> {
    T1 member1;
    T2 member2;

    public T1 getMember1() {
        return this.member1;
    }

    public void setMember1(T1 member1) {
        this.member1 = member1;
    }

    public T2 getMember2() {
        return this.member2;
    }

    public void setMember2(T2 member) {
        this.member2 = member;
    }
}
