package com.podosoft.zenela.Models;

import java.io.Serializable;

public class LikingPossibility implements Serializable {
    private Long totalLikes;

    private int possible;

    public LikingPossibility() {
    }

    public LikingPossibility(Long totalLikes, int possible) {
        this.totalLikes = totalLikes;
        this.possible = possible;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getPossible() {
        return possible;
    }

    public void setPossible(int possible) {
        this.possible = possible;
    }
}
