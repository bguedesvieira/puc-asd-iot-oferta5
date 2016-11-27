/*package br.com.monitoramento.bo;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "CHN_CONFIG")
public class ChannelConfigBo implements Serializable {
    @Id
    @GeneratedValue
    @Column(name = "CHN_ID", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private UserBo user;

    @OneToOne
    @JoinColumn(name = "PER_ID")
    private PeriodoBo periodo;

    @Column(name = "CHN_QTD_MAXIMA", nullable = false)
    private Double maxValue;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserBo getUser() {
        return user;
    }

    public void setUser(UserBo user) {
        this.user = user;
    }

    public PeriodoBo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(PeriodoBo periodo) {
        this.periodo = periodo;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
*/