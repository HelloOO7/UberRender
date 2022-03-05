package urender.demo.editor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.PlainDocument;
import org.joml.Vector4f;
import urender.api.UShaderType;
import urender.common.fs.FSUtil;
import urender.engine.UShadingMethod;
import urender.engine.UGfxObject;
import urender.engine.UMaterial;
import urender.engine.UTexture;
import urender.engine.UTextureMapper;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.engine.shader.UUniformInt;
import urender.engine.shader.UUniformVector2;
import urender.engine.shader.UUniformVector3;
import urender.engine.shader.UUniformVector4;
import urender.g3dio.generic.DDSTextureLoader;
import urender.g3dio.generic.IIOTextureLoader;
import urender.g3dio.generic.OBJModelLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxMaterialEditor extends javax.swing.JFrame {

	//Used for both the JList and the selector comboboxes
	private final SynchronizedListModel<UShader, ShaderEditHandle> shaderListModel = new SynchronizedListModel<UShader, ShaderEditHandle>(new ArrayList()) {
		@Override
		public ShaderEditHandle createHandle(UShader element) {
			return new ShaderEditHandle(element);
		}
	};
	private final SynchronizedListModel<UShaderProgram, ShaderProgramEditHandle> programListModel = new SynchronizedListModel<UShaderProgram, ShaderProgramEditHandle>(new ArrayList<>()) {
		@Override
		public ShaderProgramEditHandle createHandle(UShaderProgram element) {
			return new ShaderProgramEditHandle(element, shaderListModel);
		}
	};
	private final SynchronizedListModel<UTexture, TextureEditHandle> textureListModel = new SynchronizedListModel<UTexture, TextureEditHandle>(new ArrayList<>()) {
		@Override
		public TextureEditHandle createHandle(UTexture element) {
			return new TextureEditHandle(element);
		}
	};
	private final SynchronizedListModel<UMaterial, MaterialEditHandle> materialListModel = new SynchronizedListModel<UMaterial, MaterialEditHandle>(new ArrayList<>()) {
		@Override
		public MaterialEditHandle createHandle(UMaterial element) {
			return new MaterialEditHandle(element, programListModel, textureListModel);
		}
	};
	private final SynchronizedListModel<UUniform, UniformEditHandle> uniformListModel = new SynchronizedListModel<UUniform, UniformEditHandle>(new ArrayList<>()) {
		@Override
		public UniformEditHandle createHandle(UUniform element) {
			return new UniformEditHandle(element);
		}
	};

	/**
	 * Creates new form GfxMaterialEditor
	 */
	public GfxMaterialEditor() {
		initComponents();
		texPreviewSP.getVerticalScrollBar().setUnitIncrement(8);
		texPreviewSP.getHorizontalScrollBar().setUnitIncrement(8);

		shaderList.setModel(shaderListModel);
		shaderList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				ShaderEditHandle handle = shaderList.getSelectedValue();
				if (handle != null) {
					shaderEditorArea.setDocument(handle.document);
				} else {
					shaderEditorArea.setDocument(new PlainDocument());
				}
			}
		}));

		addRemoveShader.bind(shaderList, shaderListModel, new AddRemoveItemButtons.SingleHandler<UShader>() {
			@Override
			public UShader addSingle() {
				File shaderFile = EditorUIUtility.callFileSelect(GfxMaterialEditor.this, false, "GLSL Shader | *.vsh, *.fsh", ".vsh", ".fsh");
				if (shaderFile != null) {
					try {
						return new UShader(
							shaderFile.getName(),
							shaderFile.getName().endsWith(".fsh") ? UShaderType.FRAGMENT : UShaderType.VERTEX,
							new String(Files.readAllBytes(shaderFile.toPath()), StandardCharsets.UTF_8)
						);
					} catch (IOException ex) {
						Logger.getLogger(GfxMaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				return null;
			}

			@Override
			public boolean remove(UShader value) {
				String name = value.getName();
				for (ShaderProgramEditHandle pe : programListModel) {
					if (Objects.equals(pe.program.getFshName(), name) || Objects.equals(pe.program.getVshName(), name)) {
						EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Shader in use", "Please detach this shader from any programs in order to remove it.");
						return false;
					}
				}
				return true;
			}
		});

		shaderProgramList.setModel(programListModel);
		shaderProgramList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				ShaderProgramEditHandle handle = shaderProgramList.getSelectedValue();
				if (handle != null) {
					shaderSelectF.setModel(handle.shaderSelectF);
					shaderSelectV.setModel(handle.shaderSelectV);
				} else {
					shaderSelectF.setModel(new DefaultComboBoxModel<>());
					shaderSelectV.setModel(new DefaultComboBoxModel<>());
				}
			}
		}));

		addRemoveProgram.bind(shaderProgramList, programListModel, new AddRemoveItemButtons.SingleHandler<UShaderProgram>() {
			@Override
			public UShaderProgram addSingle() {
				String name = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "Name input", "Enter a name for the shader program");
				if (name != null) {
					UShaderProgram program = new UShaderProgram(name, null, null);
					return program;
				}
				return null;
			}

			@Override
			public boolean remove(UShaderProgram value) {
				for (MaterialEditHandle mat : materialListModel) {
					if (Objects.equals(mat.material.getShaderProgramName(), value.getName())) {
						EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Shader program in use", "Please detach this shader program from any materials in order to remove it.");
						return false;
					}
				}
				return true;
			}
		});

		materialList.setModel(materialListModel);
		materialList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				reloadMaterial();
			}
		}));

		uniformList.setModel(uniformListModel);

		addRemoveShaderParam.bind(uniformList, uniformListModel, new AddRemoveItemButtons.SingleHandler<UUniform>() {
			@Override
			public UUniform addSingle() {
				String[] options = new String[]{"Float", "Integer", "Vector2", "Vector3", "Vector4"};

				int uniformType = JOptionPane.showOptionDialog(
					GfxMaterialEditor.this,
					"Select the uniform type",
					"New shader parameter",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					"Float"
				);
				if (uniformType == JOptionPane.CLOSED_OPTION) {
					return null;
				} else {
					String uniformName = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "New shader parameter", "Enter a name for the uniform");

					if (uniformName != null) {
						switch (options[uniformType]) {
							case "Float":
								return new UUniformFloat(uniformName);
							case "Integer":
								return new UUniformInt(uniformName);
							case "Vector2":
								return new UUniformVector2(uniformName);
							case "Vector3":
								return new UUniformVector3(uniformName);
							case "Vector4":
								return new UUniformVector4(uniformName);
						}
					}
				}
				return null;
			}

			@Override
			public boolean remove(UUniform value) {
				return true;
			}
		});

		uniformList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				UniformEditHandle hnd = uniformList.getSelectedValue();
				if (hnd != null) {
					uniformEditorHolder.setViewportView((Component) hnd.getEditor());
				} else {
					uniformEditorHolder.setViewportView(null);
				}
			}
		}));

		textureMapperBox.addActionListener((e) -> {
			TextureMapperEditHandle h = (TextureMapperEditHandle) textureMapperBox.getSelectedItem();
			if (h != null) {
				textureMapperEditorHolder.setViewportView(h.editor);
			} else {
				textureMapperEditorHolder.setViewportView(null);
			}
		});

		textureList.setModel(textureListModel);

		textureList.addListSelectionListener(((e) -> {
			if (!e.getValueIsAdjusting()) {
				TextureEditHandle h = textureList.getSelectedValue();
				if (h != null && h.icon != null) {
					texPreviewHolder.setIcon(h.icon);
					texPreviewHolder.setText(null);
				} else {
					texPreviewHolder.setIcon(null);
					texPreviewHolder.setText("Preview not available.");
				}
			}
		}));

		addRemoveTexture.bind(textureList, textureListModel, new AddRemoveItemButtons.Handler<UTexture>() {
			@Override
			public List<UTexture> add() {
				List<File> files = callTextureSelect(true);
				List<UTexture> textures = new ArrayList<>();

				OverwriteMode overwriteMode = OverwriteMode.NOT_SET;

				for (File f : files) {
					String name = FSUtil.getFileNameWithoutExtension(f.getName());
					boolean doOverwrite = true;
					if (textureListModel.findHandleByName(name) != null) {
						doOverwrite = false;

						switch (overwriteMode) {
							case NO_TO_ALL:
							case YES_TO_ALL:
								break;
							default:
								overwriteMode = openOverwriteModeDialog();
								break;
						}

						switch (overwriteMode) {
							case NO:
							case NO_TO_ALL:
								doOverwrite = false;
								break;
							case YES:
							case YES_TO_ALL:
								doOverwrite = true;
								break;
						}
					}

					if (doOverwrite) {
						//Do not import if name matches
						UTexture tex = doImportTexture(f);

						if (tex != null) {
							textures.add(tex);
						}
					}
				}
				return textures;
			}

			@Override
			public boolean remove(UTexture value) {
				if (textureListModel.isUnique(value)) {
					for (MaterialEditHandle mat : materialListModel) {
						for (UTextureMapper mapper : mat.material.getTextureMappers()) {
							if (Objects.equals(mapper.getTextureName(), value.getName())) {
								EditorUIUtility.showInfoMessage(GfxMaterialEditor.this, "Texture in use", "Please detach this texture from any texture mappers in order to remove it.");
								return false;
							}
						}
					}
				}
				return true;
			}
		});
	}

	private OverwriteMode openOverwriteModeDialog() {
		int retval = JOptionPane.showOptionDialog(
			this,
			"A resource with this name already exists. Overwrite?",
			"Resource name conflict",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			new String[]{"Yes", "No", "Yes to all", "No to all"},
			"Yes"
		);
		switch (retval) {
			case 0:
				return OverwriteMode.YES;
			case 1:
				return OverwriteMode.NO;
			case 2:
				return OverwriteMode.YES_TO_ALL;
			case 3:
				return OverwriteMode.NO_TO_ALL;
			default:
				return OverwriteMode.NO;
		}
	}

	private static enum OverwriteMode {
		NOT_SET,
		YES,
		NO,
		NO_TO_ALL,
		YES_TO_ALL
	}

	private File savedata_File;
	private USceneNode savedata_SceneNode;

	private void loadResource(File file) {
		USceneNode node = new USceneNode();
		UGfxResource.loadResourceFile(file, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(node));
		savedata_File = file;
		loadSceneNode(node);
	}

	private void loadSceneNode(USceneNode node) {
		shaderList.clearSelection();
		materialList.clearSelection();
		shaderProgramList.clearSelection();
		uniformList.clearSelection();
		textureMapperBox.setSelectedIndex(-1);
		textureList.clearSelection();

		textureListModel.setList(node.textures); //ORDER IS IMPORTANT
		shaderListModel.setList(node.shaders);
		programListModel.setList(node.programs);
		materialListModel.setList(node.materials);

		savedata_SceneNode = node;
	}

	private void reloadMaterial() {
		MaterialEditHandle handle = materialList.getSelectedValue();
		IEditHandle.saveAll(uniformListModel);
		uniformList.clearSelection();
		if (handle != null) {
			shaderProgramSelect.setModel(handle.programSelect);
			uniformListModel.setList(handle.material.shaderParams);
			textureMapperBox.setModel(handle.texMapperListModel);
			shadingLayer.setModel(handle.shadingMethodModel);
			shadingPriority.setModel(handle.shadingPriorityModel);
			addRemoveTextureMapper.bind(textureMapperBox, handle.texMapperListModel, new AddRemoveItemButtons.SingleHandler<UTextureMapper>() {
				@Override
				public UTextureMapper addSingle() {
					String name = EditorUIUtility.callNameInput(GfxMaterialEditor.this, "New texture mapper", "Enter the name for the sampler uniform");
					if (name != null) {
						UTextureMapper mapper = new UTextureMapper();
						mapper.setShaderVariableName(name);
						return mapper;
					}
					return null;
				}

				@Override
				public boolean remove(UTextureMapper value) {
					return true;
				}
			});
		} else {
			shaderProgramSelect.setModel(new DefaultComboBoxModel<>());
			uniformListModel.setList(new ArrayList<>());
			textureMapperBox.setModel(new DefaultComboBoxModel<>());
			addRemoveTextureMapper.bind((JComboBox) null, null, null);
		}
		textureMapperBox.setSelectedIndex(Math.min(0, textureMapperBox.getItemCount() - 1));
	}

	private List<File> callTextureSelect(boolean multiple) {
		return EditorUIUtility.callFileSelect(GfxMaterialEditor.this, false, multiple, "Image texture | *.png, *.jpg", "*.dds", ".png", ".jpg", ".dds");
	}

	private UTexture doImportTexture(File f) {
		UTexture tex;
		if (f.getName().endsWith(".dds")) {
			tex = DDSTextureLoader.createDDSTexture(f);
		} else {
			tex = IIOTextureLoader.createIIOTexture(f);
		}
		return tex;
	}

	private File getSelectedGFXFile(boolean save) {
		return EditorUIUtility.callFileSelect(this, save, "UberRender Graphics Resource | *.gfx", ".gfx");
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorTabs = new javax.swing.JTabbedPane();
        materialEditPanel = new javax.swing.JPanel();
        materialSelectPanel = new javax.swing.JPanel();
        materialListSP = new javax.swing.JScrollPane();
        materialList = new javax.swing.JList<>();
        materialEditorTabs = new javax.swing.JTabbedPane();
        textureMapperEditor = new javax.swing.JPanel();
        texMapperLabel = new javax.swing.JLabel();
        textureMapperBox = new javax.swing.JComboBox<>();
        texMapperHeaderSep = new javax.swing.JSeparator();
        textureMapperEditorHolder = new javax.swing.JScrollPane();
        addRemoveTextureMapper = new urender.demo.editor.AddRemoveItemButtons();
        shaderParamEditor = new javax.swing.JPanel();
        shaderProgramSelectLabel = new javax.swing.JLabel();
        shaderProgramSelect = new javax.swing.JComboBox<>();
        shaderParamHeaderSep = new javax.swing.JSeparator();
        uniformListSP = new javax.swing.JScrollPane();
        uniformList = new javax.swing.JList<>();
        addRemoveShaderParam = new urender.demo.editor.AddRemoveItemButtons();
        uniformEditorHolder = new javax.swing.JScrollPane();
        layeringEditor = new javax.swing.JPanel();
        shadingLayerLabel = new javax.swing.JLabel();
        shadingLayer = new javax.swing.JComboBox<>();
        shadingPriorityLabel = new javax.swing.JLabel();
        shadingPriority = new javax.swing.JSpinner();
        debugUtilPanel = new javax.swing.JPanel();
        btnMatSetTurbo = new javax.swing.JButton();
        btnTurboEnableNrmSpec = new javax.swing.JCheckBox();
        btnTurboEnableShadowBake = new javax.swing.JCheckBox();
        btnTurboEnableLightBake = new javax.swing.JCheckBox();
        btnAllAutoTurbo = new javax.swing.JButton();
        btnTurboEnableEmi = new javax.swing.JCheckBox();
        shaderProgramEditPanel = new javax.swing.JPanel();
        shaderProgramSelectPanel = new javax.swing.JPanel();
        shaderProgramListSP = new javax.swing.JScrollPane();
        shaderProgramList = new javax.swing.JList<>();
        addRemoveProgram = new urender.demo.editor.AddRemoveItemButtons();
        shaderProgramEditor = new javax.swing.JPanel();
        prgVshLabel = new javax.swing.JLabel();
        shaderSelectV = new javax.swing.JComboBox<>();
        prgFshLabel = new javax.swing.JLabel();
        shaderSelectF = new javax.swing.JComboBox<>();
        shaderEditPanel = new javax.swing.JPanel();
        shaderSelectPanel = new javax.swing.JPanel();
        shaderListSP = new javax.swing.JScrollPane();
        shaderList = new javax.swing.JList<>();
        addRemoveShader = new urender.demo.editor.AddRemoveItemButtons();
        shaderEditor = new javax.swing.JPanel();
        shaderEditorAreaSP = new javax.swing.JScrollPane();
        shaderEditorArea = new javax.swing.JTextArea();
        textureEditPanel = new javax.swing.JPanel();
        textureListPanel = new javax.swing.JPanel();
        textureListSP = new javax.swing.JScrollPane();
        textureList = new javax.swing.JList<>();
        addRemoveTexture = new urender.demo.editor.AddRemoveItemButtons();
        btnTexReplace = new javax.swing.JButton();
        texPreviewPanel = new javax.swing.JPanel();
        texPreviewSP = new javax.swing.JScrollPane();
        texPreviewHolder = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        btnOpen = new javax.swing.JMenuItem();
        btnOpenOBJ = new javax.swing.JMenuItem();
        btnSave = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        btnReplaceMaterials = new javax.swing.JMenuItem();
        btnDeferredToForward = new javax.swing.JMenuItem();
        btnForwardToDeferred = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("uGFX MaterialEditor");
        setLocationByPlatform(true);

        materialSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Material select"));

        materialListSP.setViewportView(materialList);

        javax.swing.GroupLayout materialSelectPanelLayout = new javax.swing.GroupLayout(materialSelectPanel);
        materialSelectPanel.setLayout(materialSelectPanelLayout);
        materialSelectPanelLayout.setHorizontalGroup(
            materialSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(materialListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addContainerGap())
        );
        materialSelectPanelLayout.setVerticalGroup(
            materialSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(materialListSP)
                .addContainerGap())
        );

        texMapperLabel.setText("Texture mapper");

        textureMapperEditorHolder.setBorder(null);

        javax.swing.GroupLayout textureMapperEditorLayout = new javax.swing.GroupLayout(textureMapperEditor);
        textureMapperEditor.setLayout(textureMapperEditorLayout);
        textureMapperEditorLayout.setHorizontalGroup(
            textureMapperEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, textureMapperEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textureMapperEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textureMapperEditorHolder)
                    .addComponent(texMapperHeaderSep, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, textureMapperEditorLayout.createSequentialGroup()
                        .addComponent(texMapperLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textureMapperBox, 0, 211, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRemoveTextureMapper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        textureMapperEditorLayout.setVerticalGroup(
            textureMapperEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textureMapperEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textureMapperEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addRemoveTextureMapper, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textureMapperBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(texMapperLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(texMapperHeaderSep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textureMapperEditorHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );

        materialEditorTabs.addTab("Textures", textureMapperEditor);

        shaderProgramSelectLabel.setText("Shader program");

        shaderProgramSelect.setMaximumRowCount(20);
        shaderProgramSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shaderProgramSelectActionPerformed(evt);
            }
        });

        uniformListSP.setViewportView(uniformList);

        javax.swing.GroupLayout shaderParamEditorLayout = new javax.swing.GroupLayout(shaderParamEditor);
        shaderParamEditor.setLayout(shaderParamEditorLayout);
        shaderParamEditorLayout.setHorizontalGroup(
            shaderParamEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderParamEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderParamEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shaderParamHeaderSep)
                    .addGroup(shaderParamEditorLayout.createSequentialGroup()
                        .addComponent(shaderProgramSelectLabel)
                        .addGap(30, 30, 30)
                        .addComponent(shaderProgramSelect, 0, 267, Short.MAX_VALUE))
                    .addGroup(shaderParamEditorLayout.createSequentialGroup()
                        .addComponent(addRemoveShaderParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(shaderParamEditorLayout.createSequentialGroup()
                        .addComponent(uniformListSP, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(uniformEditorHolder)))
                .addContainerGap())
        );
        shaderParamEditorLayout.setVerticalGroup(
            shaderParamEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderParamEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderParamEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shaderProgramSelectLabel)
                    .addComponent(shaderProgramSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shaderParamHeaderSep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shaderParamEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uniformEditorHolder)
                    .addComponent(uniformListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveShaderParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        materialEditorTabs.addTab("Shader parameters", shaderParamEditor);

        shadingLayerLabel.setText("Shading layer");

        shadingPriorityLabel.setText("Priority");

        javax.swing.GroupLayout layeringEditorLayout = new javax.swing.GroupLayout(layeringEditor);
        layeringEditor.setLayout(layeringEditorLayout);
        layeringEditorLayout.setHorizontalGroup(
            layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layeringEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shadingLayerLabel)
                    .addComponent(shadingPriorityLabel))
                .addGap(18, 18, 18)
                .addGroup(layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shadingLayer, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shadingPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );
        layeringEditorLayout.setVerticalGroup(
            layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layeringEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shadingLayerLabel)
                    .addComponent(shadingLayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layeringEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shadingPriorityLabel)
                    .addComponent(shadingPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(269, Short.MAX_VALUE))
        );

        materialEditorTabs.addTab("Layering", layeringEditor);

        btnMatSetTurbo.setText("Set Turbo shader parameters");
        btnMatSetTurbo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatSetTurboActionPerformed(evt);
            }
        });

        btnTurboEnableNrmSpec.setSelected(true);
        btnTurboEnableNrmSpec.setText("Normal/Specular texture");

        btnTurboEnableShadowBake.setSelected(true);
        btnTurboEnableShadowBake.setText("Shadow bake map");

        btnTurboEnableLightBake.setSelected(true);
        btnTurboEnableLightBake.setText("Light bake map");

        btnAllAutoTurbo.setText("AutoTurbo all");
        btnAllAutoTurbo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllAutoTurboActionPerformed(evt);
            }
        });

        btnTurboEnableEmi.setText("Emission texture");

        javax.swing.GroupLayout debugUtilPanelLayout = new javax.swing.GroupLayout(debugUtilPanel);
        debugUtilPanel.setLayout(debugUtilPanelLayout);
        debugUtilPanelLayout.setHorizontalGroup(
            debugUtilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(debugUtilPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(debugUtilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTurboEnableLightBake)
                    .addComponent(btnTurboEnableShadowBake)
                    .addGroup(debugUtilPanelLayout.createSequentialGroup()
                        .addComponent(btnTurboEnableNrmSpec)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnTurboEnableEmi))
                    .addComponent(btnMatSetTurbo)
                    .addComponent(btnAllAutoTurbo))
                .addContainerGap(158, Short.MAX_VALUE))
        );
        debugUtilPanelLayout.setVerticalGroup(
            debugUtilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(debugUtilPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMatSetTurbo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(debugUtilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTurboEnableNrmSpec)
                    .addComponent(btnTurboEnableEmi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTurboEnableShadowBake)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTurboEnableLightBake)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAllAutoTurbo)
                .addContainerGap(191, Short.MAX_VALUE))
        );

        materialEditorTabs.addTab("Debug Utility", debugUtilPanel);

        javax.swing.GroupLayout materialEditPanelLayout = new javax.swing.GroupLayout(materialEditPanel);
        materialEditPanel.setLayout(materialEditPanelLayout);
        materialEditPanelLayout.setHorizontalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(materialSelectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(materialEditorTabs)
                .addContainerGap())
        );
        materialEditPanelLayout.setVerticalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(materialEditorTabs)
                    .addComponent(materialSelectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        editorTabs.addTab("Material editor", materialEditPanel);

        shaderProgramSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader program select"));

        shaderProgramListSP.setViewportView(shaderProgramList);

        javax.swing.GroupLayout shaderProgramSelectPanelLayout = new javax.swing.GroupLayout(shaderProgramSelectPanel);
        shaderProgramSelectPanel.setLayout(shaderProgramSelectPanelLayout);
        shaderProgramSelectPanelLayout.setHorizontalGroup(
            shaderProgramSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderProgramSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderProgramSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shaderProgramListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(shaderProgramSelectPanelLayout.createSequentialGroup()
                        .addComponent(addRemoveProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        shaderProgramSelectPanelLayout.setVerticalGroup(
            shaderProgramSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderProgramSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderProgramListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveProgram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        shaderProgramEditor.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader bindings"));

        prgVshLabel.setText("Vertex shader");

        shaderSelectV.setMaximumRowCount(20);
        shaderSelectV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shaderSelectVActionPerformed(evt);
            }
        });

        prgFshLabel.setText("Fragment shader");

        shaderSelectF.setMaximumRowCount(20);
        shaderSelectF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shaderSelectFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout shaderProgramEditorLayout = new javax.swing.GroupLayout(shaderProgramEditor);
        shaderProgramEditor.setLayout(shaderProgramEditorLayout);
        shaderProgramEditorLayout.setHorizontalGroup(
            shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderProgramEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(prgFshLabel)
                    .addComponent(prgVshLabel))
                .addGap(18, 18, 18)
                .addGroup(shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shaderSelectV, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shaderSelectF, 0, 267, Short.MAX_VALUE))
                .addContainerGap())
        );
        shaderProgramEditorLayout.setVerticalGroup(
            shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderProgramEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prgVshLabel)
                    .addComponent(shaderSelectV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shaderProgramEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prgFshLabel)
                    .addComponent(shaderSelectF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout shaderProgramEditPanelLayout = new javax.swing.GroupLayout(shaderProgramEditPanel);
        shaderProgramEditPanel.setLayout(shaderProgramEditPanelLayout);
        shaderProgramEditPanelLayout.setHorizontalGroup(
            shaderProgramEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderProgramEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderProgramSelectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shaderProgramEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        shaderProgramEditPanelLayout.setVerticalGroup(
            shaderProgramEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shaderProgramEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderProgramEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(shaderProgramEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shaderProgramSelectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        editorTabs.addTab("Shader program editor", shaderProgramEditPanel);

        shaderSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader select"));

        shaderListSP.setViewportView(shaderList);

        javax.swing.GroupLayout shaderSelectPanelLayout = new javax.swing.GroupLayout(shaderSelectPanel);
        shaderSelectPanel.setLayout(shaderSelectPanelLayout);
        shaderSelectPanelLayout.setHorizontalGroup(
            shaderSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shaderListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(shaderSelectPanelLayout.createSequentialGroup()
                        .addComponent(addRemoveShader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        shaderSelectPanelLayout.setVerticalGroup(
            shaderSelectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderSelectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRemoveShader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        shaderEditor.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader code"));

        shaderEditorArea.setColumns(20);
        shaderEditorArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        shaderEditorArea.setRows(5);
        shaderEditorAreaSP.setViewportView(shaderEditorArea);

        javax.swing.GroupLayout shaderEditorLayout = new javax.swing.GroupLayout(shaderEditor);
        shaderEditor.setLayout(shaderEditorLayout);
        shaderEditorLayout.setHorizontalGroup(
            shaderEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderEditorAreaSP, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );
        shaderEditorLayout.setVerticalGroup(
            shaderEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderEditorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderEditorAreaSP)
                .addContainerGap())
        );

        javax.swing.GroupLayout shaderEditPanelLayout = new javax.swing.GroupLayout(shaderEditPanel);
        shaderEditPanel.setLayout(shaderEditPanelLayout);
        shaderEditPanelLayout.setHorizontalGroup(
            shaderEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shaderEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shaderSelectPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shaderEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        shaderEditPanelLayout.setVerticalGroup(
            shaderEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shaderEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shaderEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(shaderEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shaderSelectPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        editorTabs.addTab("Shader editor", shaderEditPanel);

        textureListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Texture select"));

        textureListSP.setViewportView(textureList);

        btnTexReplace.setText("Replace");
        btnTexReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTexReplaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout textureListPanelLayout = new javax.swing.GroupLayout(textureListPanel);
        textureListPanel.setLayout(textureListPanelLayout);
        textureListPanelLayout.setHorizontalGroup(
            textureListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textureListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textureListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textureListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(textureListPanelLayout.createSequentialGroup()
                        .addComponent(addRemoveTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTexReplace)))
                .addContainerGap())
        );
        textureListPanelLayout.setVerticalGroup(
            textureListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textureListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textureListSP, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(textureListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addRemoveTexture, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTexReplace))
                .addContainerGap())
        );

        texPreviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));

        texPreviewHolder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        texPreviewSP.setViewportView(texPreviewHolder);

        javax.swing.GroupLayout texPreviewPanelLayout = new javax.swing.GroupLayout(texPreviewPanel);
        texPreviewPanel.setLayout(texPreviewPanelLayout);
        texPreviewPanelLayout.setHorizontalGroup(
            texPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(texPreviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(texPreviewSP, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );
        texPreviewPanelLayout.setVerticalGroup(
            texPreviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(texPreviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(texPreviewSP)
                .addContainerGap())
        );

        javax.swing.GroupLayout textureEditPanelLayout = new javax.swing.GroupLayout(textureEditPanel);
        textureEditPanel.setLayout(textureEditPanelLayout);
        textureEditPanelLayout.setHorizontalGroup(
            textureEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textureEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textureListPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(texPreviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        textureEditPanelLayout.setVerticalGroup(
            textureEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, textureEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textureEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(texPreviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textureListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        editorTabs.addTab("Texture editor", textureEditPanel);

        fileMenu.setText("File");

        btnOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnOpen.setText("Open GFX file");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        fileMenu.add(btnOpen);

        btnOpenOBJ.setText("Convert OBJ file");
        btnOpenOBJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenOBJActionPerformed(evt);
            }
        });
        fileMenu.add(btnOpenOBJ);

        btnSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        fileMenu.add(btnSave);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        btnReplaceMaterials.setText("Replace materials/textures");
        btnReplaceMaterials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplaceMaterialsActionPerformed(evt);
            }
        });
        editMenu.add(btnReplaceMaterials);

        btnDeferredToForward.setText("Deferred -> Forward");
        btnDeferredToForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeferredToForwardActionPerformed(evt);
            }
        });
        editMenu.add(btnDeferredToForward);

        btnForwardToDeferred.setText("Forward -> Deferred");
        btnForwardToDeferred.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardToDeferredActionPerformed(evt);
            }
        });
        editMenu.add(btnForwardToDeferred);

        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editorTabs)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editorTabs)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
		File f = getSelectedGFXFile(false);
		if (f != null) {
			loadResource(f);
		}
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		if (savedata_File == null) {
			savedata_File = getSelectedGFXFile(true);
		}
		if (savedata_File != null) {
			IEditHandle.saveAll(shaderListModel);
			IEditHandle.saveAll(programListModel);
			IEditHandle.saveAll(materialListModel);
			IEditHandle.saveAll(uniformListModel);
			IEditHandle.saveAll(textureListModel);
			for (MaterialEditHandle mat : materialListModel) {
				IEditHandle.saveAll(mat.texMapperListModel);
			}
			UGfxResource.writeResourceFile(savedata_File, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(savedata_SceneNode));
		}
    }//GEN-LAST:event_btnSaveActionPerformed

    private void shaderProgramSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shaderProgramSelectActionPerformed
		MaterialEditHandle hnd = materialList.getSelectedValue();
		if (hnd != null) {
			hnd.save();
		}
    }//GEN-LAST:event_shaderProgramSelectActionPerformed

    private void btnOpenOBJActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenOBJActionPerformed
		File objFile = EditorUIUtility.callFileSelect(this, false, "Wavefront OBJ | *.obj", ".obj");
		if (objFile != null) {
			loadSceneNode(OBJModelLoader.createOBJModelSceneNode(objFile));
			savedata_File = null;
		}
    }//GEN-LAST:event_btnOpenOBJActionPerformed

	private void setTexNameExt(UTextureMapper mapper, String baseName, String ext) {
		if (baseName != null) {
			TextureEditHandle tex = textureListModel.findHandleByName(baseName + ext);
			if (tex != null) {
				mapper.setTexture(tex.tex);
			} else {
				System.out.println("texnotfound " + baseName + ext);
			}
		}
	}

	private static class DummyEditHandle implements IEditHandle<UGfxObject> {

		private UGfxObject obj;

		public DummyEditHandle(UGfxObject obj) {
			this.obj = obj;
		}

		@Override
		public UGfxObject getContent() {
			return obj;
		}

		@Override
		public void save() {
		}

		@Override
		public String toString() {
			return obj.getName();
		}
	}

	private void makeTurboBase(MaterialEditHandle hnd, boolean normalMap, boolean specularMap, boolean emissionMap, boolean shadowBake, boolean lightBake) {
		float specIntensity = 0f;
		float normIntensity = 0f;
		float emiIntensity = 0f;

		boolean newAlb = hnd.texMapperListModel.getSize() == 0;

		UTextureMapper alb = newAlb ? new UTextureMapper() : hnd.texMapperListModel.getElementAt(0).mapper;
		String baseTexName = null;
		if (!Objects.equals(alb.getShaderVariableName(), "TexAlb")) {
			alb.setShaderVariableName("TexAlb");
			hnd.texMapperListModel.remove(alb);
			hnd.texMapperListModel.add(0, alb);
		}
		baseTexName = alb.getTextureName();
		if (baseTexName.endsWith("_Alb") || baseTexName.endsWith("_Alb2")) {
			baseTexName = baseTexName.substring(0, baseTexName.indexOf("_Alb"));
		}
		if (normalMap) {
			UTextureMapper nrm;
			TextureMapperEditHandle oldHnd = hnd.texMapperListModel.findHandleByName("TexNrm");
			nrm = oldHnd == null ? new UTextureMapper() : oldHnd.mapper;
			nrm.setShaderVariableName("TexNrm");
			setTexNameExt(nrm, baseTexName, "_Nrm");
			normIntensity = 1f;
			hnd.texMapperListModel.addUnique(hnd.texMapperListModel.remove(nrm), nrm);
		}
		if (specularMap) {
			UTextureMapper spm = new UTextureMapper();
			spm.setShaderVariableName("TexSpm");
			setTexNameExt(spm, baseTexName, "_Spm");
			specIntensity = 1f;
			hnd.texMapperListModel.addUnique(hnd.texMapperListModel.remove(spm), spm);
		}
		if (emissionMap) {
			UTextureMapper emm = new UTextureMapper();
			emm.setShaderVariableName("TexEmm");
			setTexNameExt(emm, baseTexName, "_Emm");
			emiIntensity = 1f;
			hnd.texMapperListModel.addUnique(hnd.texMapperListModel.remove(emm), emm);
		}
		if (shadowBake) {
			UTextureMapper shadowBakeMap = new UTextureMapper();
			shadowBakeMap.setShaderVariableName("TexShadowMap");
			hnd.texMapperListModel.addUnique(shadowBakeMap);
		}
		if (lightBake) {
			UTextureMapper lightBakeMap = new UTextureMapper();
			lightBakeMap.setShaderVariableName("TexLightMap");
			hnd.texMapperListModel.addUnique(lightBakeMap);
		}
		ShaderProgramEditHandle programHnd = programListModel.findHandleByName("Turbo");
		if (programHnd != null) {
			hnd.programSelect.setSelectedItemByName("Turbo");
		}

		SynchronizedListModel uniforms = hnd == materialList.getSelectedValue() ? this.uniformListModel : new SynchronizedListModel(hnd.material.shaderParams) {
			@Override
			public IEditHandle createHandle(Object element) {
				return new DummyEditHandle((UGfxObject) element);
			}
		};

		uniforms.addUnique(new UUniformFloat("MtlIntensity", 1f));
		uniforms.addUnique(new UUniformFloat("SpmIntensity", specIntensity));
		uniforms.addUnique(new UUniformFloat("SpmShininess", 256f));
		uniforms.addUnique(new UUniformFloat("NrmIntensity", normIntensity));
		uniforms.addUnique(new UUniformFloat("EmmIntensity", emiIntensity));
		uniforms.addUnique(new UUniformFloat("ShadowMapIntensity", 0.5f));
		uniforms.addUnique(new UUniformFloat("LightMapIntensity", 1.0f));
		uniforms.addUnique(new UUniformFloat("AOIntensity", 1f));
		uniforms.addUnique(new UUniformInt("MtlEnable", false));
		uniforms.addUnique(new UUniformInt("SpmEnable", specIntensity != 0f));
		uniforms.addUnique(new UUniformInt("NrmEnable", normIntensity != 0f));
		uniforms.addUnique(new UUniformInt("EmmEnable", emiIntensity != 0f));
		uniforms.addUnique(new UUniformInt("ShadowMapEnable", shadowBake));
		uniforms.addUnique(new UUniformInt("LightMapEnable", lightBake));
		uniforms.addUnique(new UUniformVector4("ShadowMapTransform", new Vector4f(1f, 1f, 0f, 0f)));
		uniforms.addUnique(new UUniformVector4("LightMapTransform", new Vector4f(1f, 1f, 0f, 0f)));
		uniforms.addUnique(new UUniformInt("AOEnable", shadowBake));
		uniforms.addUnique(new UUniformFloat("AlphaScale", 1f));
	}

    private void btnMatSetTurboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatSetTurboActionPerformed
		MaterialEditHandle hnd = materialList.getSelectedValue();
		if (hnd != null) {
			boolean pbr = btnTurboEnableNrmSpec.isSelected();
			boolean shadowBake = btnTurboEnableShadowBake.isSelected();
			boolean lightBake = btnTurboEnableLightBake.isSelected();

			makeTurboBase(hnd, pbr, pbr, btnTurboEnableEmi.isSelected(), shadowBake, lightBake);

			reloadMaterial();
		}
    }//GEN-LAST:event_btnMatSetTurboActionPerformed

    private void btnTexReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTexReplaceActionPerformed
		TextureEditHandle hnd = textureList.getSelectedValue();
		if (hnd != null) {
			List<File> texFile = callTextureSelect(false);
			if (!texFile.isEmpty()) {
				UTexture tex = doImportTexture(texFile.get(0));

				if (tex != null) {
					String texName = hnd.tex.getName();
					tex.renameTo(texName);
					int idx = textureListModel.remove(hnd.tex);
					textureListModel.add(idx, tex);
					textureList.setSelectedIndex(idx);
				}
			}
		}
    }//GEN-LAST:event_btnTexReplaceActionPerformed

	private boolean isTexturePresent(String texName) {
		return textureListModel.findHandleByName(texName) != null;
	}

    private void btnAllAutoTurboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllAutoTurboActionPerformed
		for (MaterialEditHandle hnd : materialListModel) {
			UMaterial mat = hnd.material;
			if (mat.getTextureMapperCount() != 0) {
				String albTexName = mat.getTextureMapper(0).getTextureName();
				if (albTexName.endsWith("_Alb") || albTexName.endsWith("_Alb2")) {
					String texNameBase = albTexName.substring(0, albTexName.indexOf("_Alb"));
					makeTurboBase(
						hnd,
						isTexturePresent(texNameBase + "_Nrm"),
						isTexturePresent(texNameBase + "_Spm"),
						isTexturePresent(texNameBase + "_Emm"),
						false,
						false
					);
				}
			}
		}
		reloadMaterial();
    }//GEN-LAST:event_btnAllAutoTurboActionPerformed

    private void shaderSelectFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shaderSelectFActionPerformed
		ShaderProgramEditHandle hnd = shaderProgramList.getSelectedValue();
		if (hnd != null) {
			hnd.program.setFsh(hnd.shaderSelectF.getSelectedItem() == null ? null : hnd.shaderSelectF.getSelectedItem().shader);
		}
    }//GEN-LAST:event_shaderSelectFActionPerformed

    private void shaderSelectVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shaderSelectVActionPerformed
		ShaderProgramEditHandle hnd = shaderProgramList.getSelectedValue();
		if (hnd != null) {
			hnd.program.setVsh(hnd.shaderSelectV.getSelectedItem() == null ? null : hnd.shaderSelectV.getSelectedItem().shader);
		}
    }//GEN-LAST:event_shaderSelectVActionPerformed

	private <T extends UGfxObject> void replaceAll(List<T> dest, List<T> source) {
		for (T obj : source) {
			int oldIndex = UGfxObject.remove(dest, obj.getName());
			dest.add(oldIndex == -1 ? dest.size() : oldIndex, obj);
		}
	}

    private void btnReplaceMaterialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplaceMaterialsActionPerformed
		File otherFile = getSelectedGFXFile(false);

		if (otherFile != null) {
			USceneNode replacement = new USceneNode();
			USceneNode toReplace = savedata_SceneNode;
			UGfxResource.loadResourceFile(otherFile, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(replacement));

			replaceAll(toReplace.textures, replacement.textures);
			replaceAll(toReplace.shaders, replacement.shaders);
			replaceAll(toReplace.programs, replacement.programs);
			replaceAll(toReplace.materials, replacement.materials);

			//reload completely
			loadSceneNode(savedata_SceneNode);
		}
    }//GEN-LAST:event_btnReplaceMaterialsActionPerformed

    private void btnDeferredToForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeferredToForwardActionPerformed
		for (MaterialEditHandle mat : materialListModel) {
			mat.shadingMethodModel.setSelectedItem(UShadingMethod.FORWARD);
			mat.save();
		}
    }//GEN-LAST:event_btnDeferredToForwardActionPerformed

    private void btnForwardToDeferredActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardToDeferredActionPerformed
        for (MaterialEditHandle mat : materialListModel) {
			mat.shadingMethodModel.setSelectedItem(UShadingMethod.DEFERRED);
			mat.save();
		}
    }//GEN-LAST:event_btnForwardToDeferredActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			Logger.getLogger(GfxMaterialEditor.class.getName()).log(Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new GfxMaterialEditor().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private urender.demo.editor.AddRemoveItemButtons addRemoveProgram;
    private urender.demo.editor.AddRemoveItemButtons addRemoveShader;
    private urender.demo.editor.AddRemoveItemButtons addRemoveShaderParam;
    private urender.demo.editor.AddRemoveItemButtons addRemoveTexture;
    private urender.demo.editor.AddRemoveItemButtons addRemoveTextureMapper;
    private javax.swing.JButton btnAllAutoTurbo;
    private javax.swing.JMenuItem btnDeferredToForward;
    private javax.swing.JMenuItem btnForwardToDeferred;
    private javax.swing.JButton btnMatSetTurbo;
    private javax.swing.JMenuItem btnOpen;
    private javax.swing.JMenuItem btnOpenOBJ;
    private javax.swing.JMenuItem btnReplaceMaterials;
    private javax.swing.JMenuItem btnSave;
    private javax.swing.JButton btnTexReplace;
    private javax.swing.JCheckBox btnTurboEnableEmi;
    private javax.swing.JCheckBox btnTurboEnableLightBake;
    private javax.swing.JCheckBox btnTurboEnableNrmSpec;
    private javax.swing.JCheckBox btnTurboEnableShadowBake;
    private javax.swing.JPanel debugUtilPanel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JTabbedPane editorTabs;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel layeringEditor;
    private javax.swing.JPanel materialEditPanel;
    private javax.swing.JTabbedPane materialEditorTabs;
    private javax.swing.JList<MaterialEditHandle> materialList;
    private javax.swing.JScrollPane materialListSP;
    private javax.swing.JPanel materialSelectPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel prgFshLabel;
    private javax.swing.JLabel prgVshLabel;
    private javax.swing.JPanel shaderEditPanel;
    private javax.swing.JPanel shaderEditor;
    private javax.swing.JTextArea shaderEditorArea;
    private javax.swing.JScrollPane shaderEditorAreaSP;
    private javax.swing.JList<ShaderEditHandle> shaderList;
    private javax.swing.JScrollPane shaderListSP;
    private javax.swing.JPanel shaderParamEditor;
    private javax.swing.JSeparator shaderParamHeaderSep;
    private javax.swing.JPanel shaderProgramEditPanel;
    private javax.swing.JPanel shaderProgramEditor;
    private javax.swing.JList<ShaderProgramEditHandle> shaderProgramList;
    private javax.swing.JScrollPane shaderProgramListSP;
    private javax.swing.JComboBox<ShaderProgramEditHandle> shaderProgramSelect;
    private javax.swing.JLabel shaderProgramSelectLabel;
    private javax.swing.JPanel shaderProgramSelectPanel;
    private javax.swing.JComboBox<ShaderEditHandle> shaderSelectF;
    private javax.swing.JPanel shaderSelectPanel;
    private javax.swing.JComboBox<ShaderEditHandle> shaderSelectV;
    private javax.swing.JComboBox<UShadingMethod> shadingLayer;
    private javax.swing.JLabel shadingLayerLabel;
    private javax.swing.JSpinner shadingPriority;
    private javax.swing.JLabel shadingPriorityLabel;
    private javax.swing.JSeparator texMapperHeaderSep;
    private javax.swing.JLabel texMapperLabel;
    private javax.swing.JLabel texPreviewHolder;
    private javax.swing.JPanel texPreviewPanel;
    private javax.swing.JScrollPane texPreviewSP;
    private javax.swing.JPanel textureEditPanel;
    private javax.swing.JList<TextureEditHandle> textureList;
    private javax.swing.JPanel textureListPanel;
    private javax.swing.JScrollPane textureListSP;
    private javax.swing.JComboBox<TextureMapperEditHandle> textureMapperBox;
    private javax.swing.JPanel textureMapperEditor;
    private javax.swing.JScrollPane textureMapperEditorHolder;
    private javax.swing.JScrollPane uniformEditorHolder;
    private javax.swing.JList<UniformEditHandle> uniformList;
    private javax.swing.JScrollPane uniformListSP;
    // End of variables declaration//GEN-END:variables
}
