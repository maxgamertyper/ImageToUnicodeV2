package com.maxgamertyper1;

public class VideoProgressBar {
    private final int totalFrames;
    private final String loadingof;

    public VideoProgressBar(int totalFrames, String loadingof) {
        this.totalFrames = totalFrames;
        this.loadingof = loadingof;
        InitiateBar();
    }

    private void InitiateBar() {
        System.out.print(loadingof + " |--------------------------------------------------| 0%");
        System.out.flush();
    }

    public void UpdateBar(int progressedFrames) {
        int percent = (int)(((double)progressedFrames / totalFrames) * 100);
        percent = (percent / 2) * 2;
        StringBuilder bar = new StringBuilder();
        for (int currentPercent = percent; currentPercent>0; currentPercent-=2) bar.append("#");
        for (int emptyPercent = 100-percent; emptyPercent>0; emptyPercent-=2) bar.append("-");
        System.out.print("\r" + loadingof + " |" + bar + "| " + percent + "%");
        System.out.flush();
    }
}
