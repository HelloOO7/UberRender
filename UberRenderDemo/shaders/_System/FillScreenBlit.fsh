#version 400 core

uniform sampler2D forwardSurface;

in vec2 gbuf_Texcoord;

out vec4 FragColor;

void main(void) {
	FragColor = texture2D(forwardSurface, gbuf_Texcoord);
}