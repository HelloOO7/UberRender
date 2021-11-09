#version 400 core

in vec4 FS_Color;

out vec4 FragColor;

void main(void) {
	FragColor = FS_Color;
}