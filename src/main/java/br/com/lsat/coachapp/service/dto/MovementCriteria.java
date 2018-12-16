package br.com.lsat.coachapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the Movement entity. This class is used in MovementResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /movements?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MovementCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter abbreviation;

    private StringFilter note;

    private LongFilter movementCategoryId;

    public MovementCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(StringFilter abbreviation) {
        this.abbreviation = abbreviation;
    }

    public StringFilter getNote() {
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public LongFilter getMovementCategoryId() {
        return movementCategoryId;
    }

    public void setMovementCategoryId(LongFilter movementCategoryId) {
        this.movementCategoryId = movementCategoryId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MovementCriteria that = (MovementCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(abbreviation, that.abbreviation) &&
            Objects.equals(note, that.note) &&
            Objects.equals(movementCategoryId, that.movementCategoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        abbreviation,
        note,
        movementCategoryId
        );
    }

    @Override
    public String toString() {
        return "MovementCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (abbreviation != null ? "abbreviation=" + abbreviation + ", " : "") +
                (note != null ? "note=" + note + ", " : "") +
                (movementCategoryId != null ? "movementCategoryId=" + movementCategoryId + ", " : "") +
            "}";
    }

}
