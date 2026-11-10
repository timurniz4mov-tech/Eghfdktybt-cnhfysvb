package ru.timurniz4mov.livingcivilizations.entity;

public enum NpcRole {
    TRAVELER("traveler", false, false),
    TRADER("trader", false, true),
    CARAVAN_TRADER("caravan_trader", false, true),
    WORKER("worker", false, false),
    FARMER("farmer", false, false),
    MINER("miner", false, false),
    BUILDER("builder", false, false),
    GUARD("guard", true, false),
    WARRIOR("warrior", true, false),
    LORD("lord", true, false),
    KING("king", true, false);

    private final String texture;
    private final boolean combat;
    private final boolean trader;

    NpcRole(String texture, boolean combat, boolean trader) {
        this.texture = texture;
        this.combat = combat;
        this.trader = trader;
    }

    public String texture() { return texture; }
    public boolean isCombat() { return combat; }
    public boolean isTrader() { return trader; }
}
