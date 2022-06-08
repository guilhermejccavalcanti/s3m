package br.ufpe.cin.mergers.util;

public enum CSDiffScript {
    ConsecutiveLines("/consecutive-lines.sh"),
    CSDiff("/csdiff.sh");

    private String path;

    CSDiffScript(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
