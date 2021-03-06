package org.sigmah.server.domain.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Category Element domain entity.
 * </p>
 * <p>
 * Category items associated to a global category type.
 * </p>
 * 
 * @author tmi
 */
@Entity
@Table(name = EntityConstants.CATEGORY_ELEMENT_TABLE)
public class CategoryElement extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -421149745257304446L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.CATEGORY_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.CATEGORY_ELEMENT_COLUMN_LABEL, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT, nullable = false)
	@NotNull
	private String label;

	@Column(name = EntityConstants.CATEGORY_ELEMENT_COLUMN_COLOR_HEX, nullable = false, length = EntityConstants.CATEGORY_ELEMENT_COLOR_HEX_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.CATEGORY_ELEMENT_COLOR_HEX_MAX_LENGTH)
	private String color;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.CATEGORY_TYPE_COLUMN_ID, nullable = false)
	@NotNull
	private CategoryType parentType;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	// constructeur

	public CategoryElement() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("color", color);
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
		if (parentType != null) {
			parentType.resetImport();
		}
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public CategoryType getParentType() {
		return parentType;
	}

	public void setParentType(CategoryType parentType) {
		this.parentType = parentType;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
