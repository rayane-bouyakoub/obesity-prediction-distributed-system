package Server.Entities;

import Enums.*;

import java.io.Serializable;

public class Patient implements Serializable {

    private int id;
    private Gender gender;

    private float age;

    private float height;

    private float weight;

    private family_history_overweight fho;

    private FAVC favc;

    private float fcvc;

    private float ncp;

    private CAEC caec;

    private SMOKE smoke;

    private float ch2o;

    private SCC scc;

    private float faf;

    private float tue;

    private CALC calc;

    private MTRANS mtrans;

    private NObeyesdad nobeyesdad;

    public NObeyesdad getDiagnosis() {
        return nobeyesdad;
    }

    public void setNobeyesdad(NObeyesdad nobeyesdad) {
        this.nobeyesdad = nobeyesdad;
    }

    public Patient(int id, Gender gender, float age, float height, float weight, family_history_overweight fho, FAVC favc, float fcvc, float ncp, CAEC caec, SMOKE smoke, float ch2o, SCC scc, float faf, float tue, CALC calc, MTRANS mtrans, NObeyesdad nobeyesdad) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.fho = fho;
        this.favc = favc;
        this.fcvc = fcvc;
        this.ncp = ncp;
        this.caec = caec;
        this.smoke = smoke;
        this.ch2o = ch2o;
        this.scc = scc;
        this.faf = faf;
        this.tue = tue;
        this.calc = calc;
        this.mtrans = mtrans;
        this.nobeyesdad = nobeyesdad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public family_history_overweight getFho() {
        return fho;
    }

    public void setFho(family_history_overweight fho) {
        this.fho = fho;
    }

    public FAVC getFavc() {
        return favc;
    }

    public void setFavc(FAVC favc) {
        this.favc = favc;
    }

    public float getFcvc() {
        return fcvc;
    }

    public void setFcvc(float fcvc) {
        this.fcvc = fcvc;
    }

    public float getNcp() {
        return ncp;
    }

    public void setNcp(float ncp) {
        this.ncp = ncp;
    }

    public CAEC getCaec() {
        return caec;
    }

    public void setCaec(CAEC caec) {
        this.caec = caec;
    }

    public SMOKE getSmoke() {
        return smoke;
    }

    public void setSmoke(SMOKE smoke) {
        this.smoke = smoke;
    }

    public float getCh2o() {
        return ch2o;
    }

    public void setCh2o(float ch2o) {
        this.ch2o = ch2o;
    }

    public SCC getScc() {
        return scc;
    }

    public void setScc(SCC scc) {
        this.scc = scc;
    }

    public float getFaf() {
        return faf;
    }

    public void setFaf(float faf) {
        this.faf = faf;
    }

    public float getTue() {
        return tue;
    }

    public void setTue(float tue) {
        this.tue = tue;
    }

    public CALC getCalc() {
        return calc;
    }

    public void setCalc(CALC calc) {
        this.calc = calc;
    }

    public MTRANS getMtrans() {
        return mtrans;
    }

    public void setMtrans(MTRANS mtrans) {
        this.mtrans = mtrans;
    }
}