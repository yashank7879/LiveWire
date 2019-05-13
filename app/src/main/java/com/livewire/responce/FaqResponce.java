package com.livewire.responce;

import java.io.Serializable;
import java.util.List;

public class FaqResponce {
    /**
     * parentId : 1
     * tittle : GENERAL QUESTIONS
     * SubQue : [{"subId":"","subQuestion":"How does Live Wire work?","ans":"Live Wire is a networking and recruitment tool. People who lack time or skills for the demands of daily life can meet and recruit retired people who offer their skills on an hourly or project basis."},{"subId":"","subQuestion":"Why should I use Live Wire chat and not share my personal contact details?","ans":"For your own privacy and protection, we recommend not sharing personal details until the booking has been made. Do not give your telephone number and do not provide your exact address when registering \u2013 your Suburb is ample to locate Live Wires near you."}]
     */

    private String parentId;
    private String tittle;
    private boolean isTitle;
    private boolean isVisible;
    private List<SubQueBean> SubQue;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public List<SubQueBean> getSubQue() {
        return SubQue;
    }

    public void setSubQue(List<SubQueBean> SubQue) {
        this.SubQue = SubQue;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public static class SubQueBean implements Serializable {
        /**
         * subId :
         * subQuestion : How does Live Wire work?
         * ans : Live Wire is a networking and recruitment tool. People who lack time or skills for the demands of daily life can meet and recruit retired people who offer their skills on an hourly or project basis.
         */

        private String subId;
        private String subQuestion;
        private String ans;

        public String getSubId() {
            return subId;
        }

        public void setSubId(String subId) {
            this.subId = subId;
        }

        public String getSubQuestion() {
            return subQuestion;
        }

        public void setSubQuestion(String subQuestion) {
            this.subQuestion = subQuestion;
        }

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
        }
    }
}
