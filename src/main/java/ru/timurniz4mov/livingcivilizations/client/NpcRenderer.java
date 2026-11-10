package ru.timurniz4mov.livingcivilizations.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import ru.timurniz4mov.livingcivilizations.entity.CivilianEntity;

public class NpcRenderer extends HumanoidMobRenderer<CivilianEntity, HumanoidModel<CivilianEntity>> {
    public NpcRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CivilianEntity entity) {
        return entity.getSkin();
    }
}
