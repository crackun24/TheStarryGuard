package xyz.starrylandserver.thestarryguard.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.starrylandserver.thestarryguard.TgMain;

public class CmdMain {
    private QueryNear queryNear;
    private QueryPoint queryPoint;
    private QueryPage queryPage;
    private TgMain main;

    public void onCommandReg(CommandDispatcher<CommandSourceStack> dispatcher, boolean check) {
        dispatcher.register(Commands.literal("tg").then(Commands.literal("near")
                .executes(queryNear::onCommandExec)));

        dispatcher.register(Commands.literal("tg").//注册查询点的指令
                then(Commands.literal("point").executes(queryPoint::onCommandExec)));

        dispatcher.register(Commands.literal("tg").//注册查询点的指令
                then(Commands.literal("page").then(Commands.argument("page", IntegerArgumentType.integer())
                .executes(queryPage::onCommandExec))));
    }

    @SubscribeEvent
    public void onRegCommand(RegisterCommandsEvent event) {
        this.queryNear = new QueryNear(main);
        this.queryPoint = new QueryPoint(main);
        this.queryPage = new QueryPage(main);

        this.onCommandReg(event.getDispatcher(), true);
    }

    public CmdMain(TgMain main) {
        this.main = main;
    }
}
