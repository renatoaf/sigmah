package org.sigmah.shared.dto.pivot.content;

import java.io.Serializable;

/**
 * @author Alex Bertram (v1.3)
 */
public class CategoryProperties implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3839058504415313394L;

	private String label;
	private Integer color;

	public CategoryProperties() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public void setColor(int r, int g, int b) {
		this.color = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public static CategoryProperties Color(int r, int g, int b) {
		CategoryProperties props = new CategoryProperties();
		props.setColor(r, g, b);
		return props;
	}
}
