package br.com.monitoramento.request;

/**
 * Created by Marcolino on 02/11/2016.
 */
public class ConfigRequest {

    private String login;
    private Integer period;
    private Double maxValue;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
