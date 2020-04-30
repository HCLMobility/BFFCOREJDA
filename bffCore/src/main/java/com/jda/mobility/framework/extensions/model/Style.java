package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Style implements Serializable {
	private static final long serialVersionUID = -7292884757834717155L;
	@SuppressWarnings("all")
	private String style;
	private String fontType;
	private String fontSize;
	private String fontColor;
	private String backgroundColor;
	private String fontWeight;
	private String width;
	private String height;
	private String padding;
	private String margin;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getFontType() {
		return fontType;
	}

	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontColor() {
		return fontColor;
	}

	@JsonProperty("fontColor")
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
	}

	public String getMargin() {
		return margin;
	}

	public void setMargin(String margin) {
		this.margin = margin;
	}	

	@Override
	public int hashCode() {
		return Objects.hash(backgroundColor, fontColor, fontSize, fontType, fontWeight, height, margin, padding, style,
				width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Style other = (Style) obj;
		return Objects.equals(backgroundColor, other.backgroundColor) && Objects.equals(fontColor, other.fontColor)
				&& Objects.equals(fontSize, other.fontSize) && Objects.equals(fontType, other.fontType)
				&& Objects.equals(fontWeight, other.fontWeight) && Objects.equals(height, other.height)
				&& Objects.equals(margin, other.margin) && Objects.equals(padding, other.padding)
				&& Objects.equals(style, other.style) && Objects.equals(width, other.width);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Style [style=").append(style).append(", fontType=").append(fontType).append(", fontSize=")
				.append(fontSize).append(", fontColor=").append(fontColor).append(", backgroundColor=")
				.append(backgroundColor).append(", fontWeight=").append(fontWeight).append(", width=").append(width)
				.append(", height=").append(height).append(", padding=").append(padding).append(", margin=")
				.append(margin).append("]");
		return builder.toString();
	}

}
