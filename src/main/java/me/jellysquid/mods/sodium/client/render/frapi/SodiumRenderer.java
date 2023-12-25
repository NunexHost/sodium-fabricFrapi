package me.jellysquid.mods.sodium.client.render.frapi;

import me.jellysquid.mods.sodium.client.render.frapi.material.MaterialFinderImpl;
import me.jellysquid.mods.sodium.client.render.frapi.material.RenderMaterialImpl;
import me.jellysquid.mods.sodium.client.render.frapi.mesh.MeshBuilderImpl;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import java.util.HashMap;

/**
 * The Sodium renderer implementation.
 */
public class SodiumRenderer implements Renderer {

    public static final SodiumRenderer INSTANCE = new SodiumRenderer();

    public static final RenderMaterial STANDARD_MATERIAL = INSTANCE.materialFinder().find();

    static {
        INSTANCE.registerMaterial(RenderMaterial.MATERIAL_STANDARD, STANDARD_MATERIAL);
    }

    private final HashMap<Identifier, RenderMaterial> materialMap = new HashMap<>();

    private SodiumRenderer() { }

    @Override
    public MeshBuilder meshBuilder() {
        return new MeshBuilderImpl();
    }

    @Override
    public MaterialFinder materialFinder() {
        return new MaterialFinderImpl();
    }

    @Override
    public RenderMaterial materialById(Identifier id) {
        return materialMap.get(id);
    }

    @Override
    public boolean registerMaterial(Identifier id, RenderMaterial material) {
        if (materialMap.containsKey(id)) return false;

        // cast to prevent acceptance of impostor implementations
        if (material instanceof RenderMaterialImpl) {
            materialMap.put(id, (RenderMaterialImpl) material);
            return true;
        }

        return false;
    }

    /**
     * Optimizes the renderer for Mali-G57 devices.
     */
    public void optimizeForMaliG57() {
        // Use 16-bit floats for shaders and textures where possible.
        this.materialFinder().setShaderPrecision(RenderMaterial.ShaderPrecision.FP16);
        this.materialFinder().setTexturePrecision(RenderMaterial.TexturePrecision.FP16);

        // Use ARM NEON intrinsics for vectorized operations.
        this.materialFinder().setShaderOptimizationFlags(RenderMaterial.ShaderOptimizationFlag.USE_NEON);

        // Optimize for Bifrost architecture.
        this.materialFinder().setShaderOptimizationFlags(RenderMaterial.ShaderOptimizationFlag.OPTIMIZE_FOR_BIFROST);
    }
}
