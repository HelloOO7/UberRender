#version 400 core

layout(location = 0) in vec3 a_Position;

uniform mat4 UBR_WorldMatrix;
uniform mat4 UBR_ProjectionMatrix;

out vec4 FS_Color;

void main(void) {
	vec4 outPosition = UBR_ProjectionMatrix * UBR_WorldMatrix * vec4(a_Position, 1.0);
	FS_Color = vec4(outPosition.xyz, 1.0);
	gl_Position = outPosition;
}