package org.gepron1x.peak.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participation")
@Getter
@Setter
@NoArgsConstructor
public class Participation {

    @EmbeddedId
    private ParticipationId id;

    @MapsId("ascentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ascent_id", nullable = false)
    private Ascent ascent;

    @MapsId("climberId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "climber_id", nullable = false)
    private Climber climber;

    public Participation(Ascent ascent, Climber climber) {
        this.ascent = ascent;
        this.climber = climber;
        this.id = new ParticipationId(ascent.getId(), climber.getId());
    }
}
