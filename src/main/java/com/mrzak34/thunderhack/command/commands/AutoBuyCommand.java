package com.mrzak34.thunderhack.command.commands;

import com.mrzak34.thunderhack.command.Command;
import com.mrzak34.thunderhack.modules.misc.AutoBuy;

import java.util.Arrays;
import java.util.Objects;

public class AutoBuyCommand extends Command {

    public AutoBuyCommand() {
        super("ab");
    }

    // .ab add bow 5000 23(2) 3(1)

    @Override
    public void execute(String[] args) {
        if (args.length >= 4) {
            if (args[0] == null) {
                Command.sendMessage(usage());
            }

            if (args[0].equals("add")) {
                String itemName = args[1];
                String price = args[2].toUpperCase();
                String ench1 = String.join(" ", Arrays.copyOfRange(args, 3, args.length - 1));
                String[] ench2 = ench1.split(" ");

                AutoBuy.AutoBuyItem item = new AutoBuy.AutoBuyItem(itemName, ench2, Integer.parseInt(price), 0, args.length == 4);
                AutoBuy.items.add(item);
                sendMessage("Добавлен предмет " + itemName + " стоимостью до " + price + (args.length == 4 ? " без чаров" : " с чарами " + ench1));
            }
        } else if (args.length > 1) {
            if (args[0].equals("list")) {
                AutoBuy.items.forEach(itm -> sendMessage("\n" +
                        "####################" + "\n" +
                        "Предмет: " + itm.getName() + "\n" +
                        "Макс цена: " + itm.getPrice1() + "\n" +
                        "Чары: " + Arrays.toString(itm.getEnchantments()) + "\n" +
                        "####################"
                ));
            }
            if (args[0].equals("remove")) {
                String itemName = args[1];
                boolean removed = false;
                for (AutoBuy.AutoBuyItem abitem : AutoBuy.items) {
                    if (Objects.equals(abitem.getName(), itemName)) {
                        AutoBuy.items.remove(abitem);
                        removed = true;
                    }
                }
                if (removed) {
                    sendMessage("Предмет успешно удален!");
                } else {
                    sendMessage("Предмета не существует!");
                }
            }
        } else {
            sendMessage(usage());
        }
    }


    String usage() {
        return ".ab add/remove/list   пример: .ab add bow 51(1) 48(5)" + "\n" + "Айди зачаров смотреть на сайте https://idpredmetov.ru/id-zacharovanij/" + "\n" + "Если левела зачара нет - пишем первый левел";
    }
}
