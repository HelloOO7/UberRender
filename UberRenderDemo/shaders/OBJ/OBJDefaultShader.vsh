#version 400 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec3 a_Normal;
layout(location = 2) in vec2 a_Texcoord0;

uniform mat4 UBR_ModelMatrix;
uniform mat4 UBR_ViewMatrix;
uniform mat4 UBR_ProjectionMatrix;
uniform mat3 UBR_NormalMatrix;

out vec3 FS_Normal;
out vec3 FS_View;
out vec2 FS_Texcoord0;

void main(void) {
	vec4 outPosition = UBR_ModelMatrix * vec4(a_Position, 1.0);
 	FS_View = outPosition.xyz; 
	outPosition = UBR_ProjectionMatrix * UBR_ViewMatrix * outPosition;
	FS_Normal = UBR_NormalMatrix * a_Normal;
	FS_Texcoord0 = a_Texcoord0;
	gl_Position = outPosition;
}