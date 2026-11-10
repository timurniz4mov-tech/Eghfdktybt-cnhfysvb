package ru.timurniz4mov.livingcivilizations.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import ru.timurniz4mov.livingcivilizations.LivingCivilizations;
import ru.timurniz4mov.livingcivilizations.entity.BanditEntity;

public class BanditRenderer extends HumanoidMobRenderer<BanditEntity, HumanoidModel<BanditEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LivingCivilizations.MOD_ID, "textures/entity/bandit.png");

    public BanditRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(BanditEntity entity) { return TEXTURE; }
}
