package net.borisshoes.mobtokens.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.village.VillagerProfession;

import java.util.UUID;

public class MobHeadUtils {
   
   public static final ItemStack BEE = makeHead(
         Text.literal("Bee Token").formatted(Formatting.BOLD,Formatting.YELLOW),
         "Bee",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlhYzE2ZjI5NmI0NjFkMDVlYTA3ODVkNDc3MDMzZTUyNzM1OGI0ZjMwYzI2NmFhMDJmMDIwMTU3ZmZjYTczNiJ9fX0=",
         new int[]{1999906402,-2005908390,-2036338514,-247436483});
   
   public static final ItemStack CHICKEN = makeHead(
         Text.literal("Chicken Token").formatted(Formatting.BOLD,Formatting.WHITE),
         "Chicken",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhZjZlNTg0N2VlYTA5OWUxYjBhYjhjMjBhOWU1ZjNjNzE5MDE1OGJkYTU0ZTI4MTMzZDliMjcxZWMwY2I0YiJ9fX0=",
         new int[]{-935129388,-883012526,-1795271171,133599976});
   
   public static final ItemStack COW = makeHead(
         Text.literal("Cow Token").formatted(Formatting.BOLD,Formatting.DARK_GRAY),
         "Cow",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkNjIxMTAwZmVhNTg4MzkyMmU3OGJiNDQ4MDU2NDQ4Yzk4M2UzZjk3ODQxOTQ4YTJkYTc0N2Q2YjA4YjhhYiJ9fX0=",
         new int[]{906227793,992953384,-1126463235,1664106940});
   
   public static final ItemStack RED_MOOSHROOM = makeHead(
         Text.literal("Red Mooshroom Token").formatted(Formatting.BOLD,Formatting.RED),
         "Red Mooshroom",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE4MDYwNmU4MmM2NDJmMTQxNTg3NzMzZTMxODBhZTU3ZjY0NjQ0MmM5ZmZmZDRlNTk5NzQ1N2UzNDMxMWEyOSJ9fX0=",
         new int[]{-1043345433,-1915205024,-1319783024,1345020847});
   
   public static final ItemStack BROWN_MOOSHROOM = makeHead(
         Text.literal("Brown Mooshroom Token").formatted(Formatting.BOLD,Formatting.GRAY),
         "Brown Mooshroom",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U2NDY2MzAyYTVhYjQzOThiNGU0NzczNDk4MDhlNWQ5NDAyZWEzYWQ4ZmM0MmUyNDQ2ZTRiZWQwYTVlZDVlIn19fQ==",
         new int[]{1343259795,-414497460,-2098198200,-2128852243});
   
   public static final ItemStack PIGLIN = makeHead(
         Text.literal("Piglin Token").formatted(Formatting.BOLD,Formatting.GOLD),
         "Piglin",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFhZmQ4NTM5MTE4MmE5ZjlkZTRmY2UyOWVhZjAyNTE0Y2MyZTA0NDgxNTc3ZGE1ZWRlYjU4YjE3ZTc1NzEzNSJ9fX0=",
         new int[]{1712944054,1882145456,-2109092431,-1550094437});
   
   public static final ItemStack PIG = makeHead(
         Text.literal("Pig Token").formatted(Formatting.BOLD,Formatting.LIGHT_PURPLE),
         "Pig",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFlZTc2ODFhZGYwMDA2N2YwNGJmNDI2MTFjOTc2NDEwNzVhNDRhZTJiMWMwMzgxZDVhYzZiMzI0NjIxMWJmZSJ9fX0=",
         new int[]{-1388919770,57427511,-1742279351,-1877871031});
   
   public static final ItemStack RABBIT = makeHead(
         Text.literal("Rabbit Token").formatted(Formatting.BOLD,Formatting.AQUA),
         "Rabbit",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZkNGY4NmNmNzQ3M2ZiYWU5M2IxZTA5MDQ4OWI2NGMwYmUxMjZjN2JiMTZmZmM4OGMwMDI0NDdkNWM3Mjc5NSJ9fX0=",
         new int[]{-1641248077,1046954686,-1366358770,-929771023});
   
   public static final ItemStack TURTLE = makeHead(
         Text.literal("Turtle Token").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Turtle",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0OTMxMjAwYWQ0NjBiNjUwYTE5MGU4ZDQxMjI3YzM5OTlmYmViOTMzYjUxY2E0OWZkOWU1OTIwZDFmOGU3ZCJ9fX0=",
         new int[]{-630254624,-837990550,-1118417637,-2131664733});
   
   
   public static final ItemStack WHITE_SHEEP = makeHead(
         Text.literal("White Sheep").formatted(Formatting.BOLD,Formatting.WHITE),
         "White Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmZTdjYzQ2ZDc0OWIxNTMyNjFjMWRjMTFhYmJmMmEzMTA4ZWExYmEwYjI2NTAyODBlZWQxNTkyZGNmYzc1YiJ9fX0=",
         new int[]{1709416521,-1703654120,-1943757901,990313825});
   
   public static final ItemStack BLACK_SHEEP = makeHead(
         Text.literal("Black Sheep").formatted(Formatting.BOLD,Formatting.DARK_GRAY),
         "Black Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMzMzVlODA2NWM3YjVkZmVhNThkM2RmNzQ3NGYzOTZhZjRmYTBhMmJhNTJhM2M5YjdmYmE2ODMxOTI3MWM5MSJ9fX0=",
         new int[]{-1067827748,-1531689024,-1996762374,-1760412468});
   
   public static final ItemStack BLUE_SHEEP = makeHead(
         Text.literal("Blue Sheep").formatted(Formatting.BOLD,Formatting.BLUE),
         "Blue Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQwZTI3N2RhNmMzOThiNzQ5YTMyZjlkMDgwZjFjZjRjNGVmM2YxZjIwZGQ5ZTVmNDIyNTA5ZTdmZjU5M2MwIn19fQ==",
         new int[]{35067668,152850181,-2055683214,-1643148636});
   
   public static final ItemStack BROWN_SHEEP = makeHead(
         Text.literal("Brown Sheep").formatted(Formatting.BOLD,Formatting.DARK_RED),
         "Brown Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEyOGQwODZiYzgxNjY5ZmMyMjU1YmIyMmNhZGM2NmEwZjVlZDcwODg1ZTg0YzMyZDM3YzFiNDg0ZGIzNTkwMSJ9fX0=",
         new int[]{-319982265,-381271998,-1214398175,-1898689453});
   
   public static final ItemStack CYAN_SHEEP = makeHead(
         Text.literal("Cyan Sheep").formatted(Formatting.BOLD,Formatting.DARK_AQUA),
         "Cyan Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ0MmZjYmNhZjlkNDhmNzNmZmIwYzNjMzZmMzRiNDY0MzI5NWY2ZGFhNmNjNzRhYjlkMjQyZWQ1YWE1NjM2In19fQ==",
         new int[]{-610664437,-297646773,-1522887944,-1496580530});
   
   public static final ItemStack GRAY_SHEEP = makeHead(
         Text.literal("Gray Sheep").formatted(Formatting.BOLD,Formatting.DARK_GRAY),
         "Gray Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhZmVjZjA2MDNiMmRjZDc5ODRkMjUyNTg2MDY5ODk1ZGI5YWE3OGUxODQxYmQ1NTRiMTk1MDhkY2Y5NjdhMSJ9fX0=",
         new int[]{-1722503086,69357176,-1410876840,1613265857});
   
   public static final ItemStack GREEN_SHEEP = makeHead(
         Text.literal("Green Sheep").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Green Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVhODg3ZWFlNGIwNzYzNmU5ZTJmOTA2NjA5YjAwYWI4ZDliODZiNzQ3MjhiODE5ZmY2ZjM3NjU4M2VhMTM5In19fQ==",
         new int[]{731104223,613632743,-1340680185,2064832692});
   
   public static final ItemStack LIGHT_BLUE_SHEEP = makeHead(
         Text.literal("Light Blue Sheep").formatted(Formatting.BOLD,Formatting.AQUA),
         "Light Blue Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWJmMjNhZjg3MTljNDM3YjNlZTg0MDE5YmEzYzllNjljYTg1NGQzYThhZmQ1Y2JhNmQ5Njk2YzA1M2I0ODYxNCJ9fX0=",
         new int[]{1363466330,1339703551,-1611029030,1799093499});
   
   public static final ItemStack LIGHT_GRAY_SHEEP = makeHead(
         Text.literal("Light Gray Sheep").formatted(Formatting.BOLD,Formatting.GRAY),
         "Light Gray Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQyZTJlOTNhMTQyYmZkNDNmMjQwZDM3ZGU4ZjliMDk3NmU3NmU2NWIyMjY1MTkwODI1OWU0NmRiNzcwZSJ9fX0=",
         new int[]{1750550694,1826508326,-1524837646,254098541});
   
   public static final ItemStack LIME_SHEEP = makeHead(
         Text.literal("Lime Sheep").formatted(Formatting.BOLD,Formatting.GREEN),
         "Lime Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJlYWQwMzQyYWU4OWI4ZGZkM2Q3MTFhNjBhZGQ2NWUyYzJiZmVhOGQwYmQyNzRhNzU4N2RlZWQ3YTMxODkyZSJ9fX0=",
         new int[]{1287435907,-218808040,-1343748755,-967007072});
   
   public static final ItemStack MAGENTA_SHEEP = makeHead(
         Text.literal("Magenta Sheep").formatted(Formatting.BOLD,Formatting.LIGHT_PURPLE),
         "Magenta Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYThlMWYwNWYwZGFjY2E2M2E3MzE4NzRmOTBhNjkzZmZlMjFmZjgzMmUyYjFlMWQwN2I2NWM4NzY0NTI2ZjA4OSJ9fX0=",
         new int[]{1295070073,749946091,-1420404987,1810598465});
   
   public static final ItemStack ORANGE_SHEEP = makeHead(
         Text.literal("Orange Sheep").formatted(Formatting.BOLD,Formatting.GOLD),
         "Orange Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjY4NGQwNGZhODBhYTU5ZGExNDUzNWRlYWQzODgzZDA5N2ZiYmE0MDA2MjU2NTlmNTI1OTk2NDgwNmJhNjZmMCJ9fX0=",
         new int[]{-1659364310,-1226226116,-1693897344,-2097332172});
   
   public static final ItemStack PINK_SHEEP = makeHead(
         Text.literal("Pink Sheep").formatted(Formatting.BOLD,Formatting.LIGHT_PURPLE),
         "Pink Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2M2U4YTkzZDI4N2E4NGU2NDAzMDlhZTgzY2ExZGUwYTBiMjU3NTA1YTIwZWM1NWIzMzQ5ZDQwYTQ0ODU0In19fQ==",
         new int[]{-305810543,789856627,-1293793230,-575937365});
   
   public static final ItemStack PURPLE_SHEEP = makeHead(
         Text.literal("Purple Sheep").formatted(Formatting.BOLD,Formatting.DARK_PURPLE),
         "Purple Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ0OWQwODI5MWRhZTQ1YTI0NjczNjE5NjAyZjQzNWI1N2Y0Y2Q0ZTllOThkMmUwZmJlYzRmMTgxNDQ3ODFkMyJ9fX0=",
         new int[]{-996311885,-1057339452,-1663293651,-449905612});
   
   public static final ItemStack RED_SHEEP = makeHead(
         Text.literal("Red Sheep").formatted(Formatting.BOLD,Formatting.RED),
         "Red Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQ3OGUwNTcxNThkZTZmNDVlMjU0MWNkMTc3ODhlNjQwY2NiNTk3MjNkZTU5YzI1NGU4MmFiNTcxMWYzZmMyNyJ9fX0=",
         new int[]{-1931790612,1757564302,-1613542270,1303824883});
   
   public static final ItemStack YELLOW_SHEEP = makeHead(
         Text.literal("Yellow Sheep").formatted(Formatting.BOLD,Formatting.YELLOW),
         "Yellow Sheep",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTRiMjhmMDM1NzM1OTA2ZjgyZmZjNGRiYTk5YzlmMGI1NTI0MGU0MjZjZDFjNTI1YTlhYTc3MTgwZWVjNDkzNCJ9fX0=",
         new int[]{-538769203,-68924429,-1873938686,-788513363});
   
   
   public static final ItemStack ARMORER_VILLAGER = makeHead(
         Text.literal("Armorer Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Armorer Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVmNjI3ZjU2NmFjMGE3ODI4YmFkOTNlOWU0Yjk2NDNkOTlhOTI4YTEzZDVmOTc3YmY0NDFlNDBkYjEzMzZiZiJ9fX0=",
         new int[]{-1627151347,-1285404754,-1966464448,95988217});
   
   public static final ItemStack BUTCHER_VILLAGER = makeHead(
         Text.literal("Butcher Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Butcher Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFiYWQ2NDE4NWUwNGJmMWRhZmUzZGE4NDkzM2QwMjU0NWVhNGE2MzIyMWExMGQwZjA3NzU5MTc5MTEyYmRjMiJ9fX0=",
         new int[]{-1958848492,-1136704916,-1204946794,981552340});
   
   public static final ItemStack CARTOGRAPHER_VILLAGER = makeHead(
         Text.literal("Cartographer Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Cartographer Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNhZWNmYmU4MDFjZjMyYjVkMWIwYjFmNjY4MDA0OTY2NjE1ODY3OGM1M2Y0YTY1MWZjODNlMGRmOWQzNzM4YiJ9fX0=",
         new int[]{-1530336204,127485113,-1129389748,648352210});
   
   public static final ItemStack CLERIC_VILLAGER = makeHead(
         Text.literal("Cleric Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Cleric Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWI5ZTU4MmUyZjliODlkNTU2ZTc5YzQ2OTdmNzA2YjFkZDQ5MjllY2FlM2MwN2VlOTBiZjFkNWJlMzE5YmY2ZiJ9fX0=",
         new int[]{-1449678884,1755333049,-1266768369,1645723165});
   
   public static final ItemStack FARMER_VILLAGER = makeHead(
         Text.literal("Farmer Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Farmer Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkyNzJkMDNjZGE2MjkwZTRkOTI1YTdlODUwYTc0NWU3MTFmZTU3NjBmNmYwNmY5M2Q5MmI4ZjhjNzM5ZGIwNyJ9fX0=",
         new int[]{-1942451583,-1297792036,-1269674622,558190098});
   
   public static final ItemStack FISHERMAN_VILLAGER = makeHead(
         Text.literal("Fisherman Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Fisherman Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE4OWZiNGFjZDE1ZDczZmYyYTU4YTg4ZGYwNDY2YWQ5ZjRjMTU0YTIwMDhlNWM2MjY1ZDVjMmYwN2QzOTM3NiJ9fX0=",
         new int[]{-1558427898,-80395654,-1539972253,-1897275374});
   
   public static final ItemStack FLETCHER_VILLAGER = makeHead(
         Text.literal("Fletcher Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Fletcher Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MTFmMTJlMThjZTQ0YTU3MjM4ZWVmMWNhZTAzY2Q5ZjczMGE3YTQ1ZTBlYzI0OGYxNGNlODRlOWM0ODA1NiJ9fX0=",
         new int[]{1972188752,1038437151,-1255133373,-1723054561});
   
   public static final ItemStack LEATHERWORKER_VILLAGER = makeHead(
         Text.literal("Leatherworker Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Leatherworker Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=",
         new int[]{1445316252,2041728101,-1840993025,-322242010});
   
   public static final ItemStack LIBRARIAN_VILLAGER = makeHead(
         Text.literal("Librarian Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Librarian Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjYWE1NzRiYWJiNDBlZTBmYTgzZjJmZDVlYTIwY2ZmMzFmZmEyNzJmZTExMzU4OGNlZWU0Njk2ODIxMjhlNyJ9fX0=",
         new int[]{2047317031,960709978,-1246891731,-1197134412});
   
   public static final ItemStack MASON_VILLAGER = makeHead(
         Text.literal("Mason Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Mason Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=",
         new int[]{1445316252,2041728101,-1840993025,-322242010});
   
   public static final ItemStack NITWIT_VILLAGER = makeHead(
         Text.literal("Nitwit Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Nitwit Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=",
         new int[]{1445316252,2041728101,-1840993025,-322242010});
   
   public static final ItemStack UNEMPLOYED_VILLAGER = makeHead(
         Text.literal("Unemployed Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Unemployed Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=",
         new int[]{1445316252,2041728101,-1840993025,-322242010});
   
   public static final ItemStack SHEPHERD_VILLAGER = makeHead(
         Text.literal("Shepherd Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Shepherd Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiZjRlOTE1NGFjOTI3MTk0MWM3MzNlYWNjNjJkYzlmYzBhNmRjMWI1ZDY3Yzc4Y2E5OGFmYjVjYjFiZTliMiJ9fX0=",
         new int[]{-1872621004,-1520941095,-1386917366,898929216});
   
   public static final ItemStack TOOLSMITH_VILLAGER = makeHead(
         Text.literal("Toolsmith Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Toolsmith Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=",
         new int[]{1445316252,2041728101,-1840993025,-322242010});
   
   public static final ItemStack WEAPONSMITH_VILLAGER = makeHead(
         Text.literal("Weaponsmith Villager").formatted(Formatting.BOLD,Formatting.GREEN),
         "Weaponsmith Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3NmZmYTQxMGJiZTdmYTcwOTA5OTY1YTEyNWY0YTRlOWE0ZmIxY2UxYjhiM2MzNGJmYjczYWFmZmQ0Y2U0MyJ9fX0=",
         new int[]{-1988786943,-471251346,-2084795059,-456600256});
   
   
   public static final ItemStack ZOMBIE_ARMORER = makeHead(
         Text.literal("Zombie Armorer").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Armorer",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2NzllMDM0NzY3ZDUxODY2MGQ5NDE2ZGM1ZWFmMzE5ZDY5NzY4MmFjNDBjODg2ZTNjMmJjOGRmYTFkZTFkIn19fQ==",
         new int[]{2096843698,839205627,-1344900867,1477425317});
   
   public static final ItemStack ZOMBIE_BUTCHER = makeHead(
         Text.literal("Zombie Butcher").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Butcher",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNjZThkNmNlNDEyNGNlYzNlODRhODUyZTcwZjUwMjkzZjI0NGRkYzllZTg1NzhmN2Q2ZDg5MjllMTZiYWQ2OSJ9fX0=",
         new int[]{1815714177,-1845539437,-1299985766,-249396350});
   
   public static final ItemStack ZOMBIE_CARTOGRAPHER = makeHead(
         Text.literal("Zombie Cartographer").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Cartographer",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTYwODAwYjAxMDEyZTk2M2U3YzIwYzhiYTE0YjcwYTAyNjRkMTQ2YTg1MGRlZmZiY2E3YmZlNTEyZjRjYjIzZCJ9fX0=",
         new int[]{-1100180737,-40612522,-1177935632,84884289});
   
   public static final ItemStack ZOMBIE_CLERIC = makeHead(
         Text.literal("Zombie Cleric").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Cleric",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ODU3OGJlMGUxMjE3MjczNGE3ODI0MmRhYjE0OTY0YWJjODVhYjliNTk2MzYxZjdjNWRhZjhmMTRhMGZlYiJ9fX0=",
         new int[]{648641787,1287409525,-1203270335,538623305});
   
   public static final ItemStack ZOMBIE_FARMER = makeHead(
         Text.literal("Zombie Farmer").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Farmer",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc3ZDQxNWY5YmFhNGZhNGI1ZTA1OGY1YjgxYmY3ZjAwM2IwYTJjOTBhNDgzMWU1M2E3ZGJjMDk4NDFjNTUxMSJ9fX0=",
         new int[]{1184252343,-325498270,-1248344102,-2059244882});
   
   public static final ItemStack ZOMBIE_FISHERMAN = makeHead(
         Text.literal("Zombie Fisherman").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Fisherman",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkwNWQ1M2ZlNGZhZWIwYjMxNWE2ODc4YzlhYjgxYjRiZTUyYzMxY2Q0NzhjMDI3ZjBkN2VjZTlmNmRhODkxNCJ9fX0=",
         new int[]{403833101,-476951492,-1268224880,1726114843});
   
   public static final ItemStack ZOMBIE_FLETCHER = makeHead(
         Text.literal("Zombie Fletcher").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Fletcher",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVhMjZhYzBlMjU0OThhZGFkYTRlY2VhNThiYjRlNzZkYTMyZDVjYTJkZTMwN2VmZTVlNDIxOGZiN2M1ZWY4OSJ9fX0=",
         new int[]{2099295440,96552589,-1593973544,1732222062});
   
   public static final ItemStack ZOMBIE_LEATHERWORKER = makeHead(
         Text.literal("Zombie Leatherworker").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Leatherworker",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=",
         new int[]{-2018152173,-657829980,-2112558488,-1763520945});
   
   public static final ItemStack ZOMBIE_LIBRARIAN = makeHead(
         Text.literal("Zombie Librarian").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Librarian",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIyMTFhMWY0MDljY2E0MjQ5YzcwZDIwY2E4MDM5OWZhNDg0NGVhNDE3NDU4YmU5ODhjYzIxZWI0Nzk3Mzc1ZSJ9fX0=",
         new int[]{543806214,-1390195783,-1496262174,-176728090});
   
   public static final ItemStack ZOMBIE_MASON = makeHead(
         Text.literal("Zombie Mason").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Mason",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=",
         new int[]{-2018152173,-657829980,-2112558488,-1763520945});
   
   public static final ItemStack ZOMBIE_NITWIT = makeHead(
         Text.literal("Zombie Nitwit").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Nitwit",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=",
         new int[]{-2018152173,-657829980,-2112558488,-1763520945});
   
   public static final ItemStack ZOMBIE_VILLAGER = makeHead(
         Text.literal("Zombie Villager").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Villager",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=",
         new int[]{-2018152173,-657829980,-2112558488,-1763520945});
   
   public static final ItemStack ZOMBIE_SHEPHERD = makeHead(
         Text.literal("Zombie Shepherd").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Shepherd",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkxMzkxYmVmM2E0NmVmMjY3ZDNiNzE3MTA4NmJhNGM4ZDE3ZjJhNmIwZjgzZmEyYWMzMGVmZTkxNGI3YzI0OSJ9fX0=",
         new int[]{1207380466,-1608759608,-1741978926,-1403436161});
   
   public static final ItemStack ZOMBIE_TOOLSMITH = makeHead(
         Text.literal("Zombie Toolsmith").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Toolsmith",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=",
         new int[]{-2018152173,-657829980,-2112558488,-1763520945});
   
   public static final ItemStack ZOMBIE_WEAPONSMITH = makeHead(
         Text.literal("Zombie Weaponsmith").formatted(Formatting.BOLD,Formatting.DARK_GREEN),
         "Zombie Weaponsmith",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3MDg5NGI1Y2MzMDVkODdhYTA4YzNiNGIwODU4N2RiNjhmZjI5ZTdhM2VmMzU0Y2FkNmFiY2E1MGU1NTI4YiJ9fX0=",
         new int[]{-1844572353,-185316455,-1901309798,-1756753005});
   
   public static final ItemStack EMPTY_TOKEN = makeHead(
         Text.literal("Empty Mob Token").formatted(Formatting.BOLD,Formatting.GRAY),
         "MHF_Question",
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZTA2M2NhZmI0NjdhNWM4ZGU0M2VjNzg2MTkzOTlmMzY5ZjRhNTI0MzRkYTgwMTdhOTgzY2RkOTI1MTZhMCJ9fX0=",
         new int[]{1617833968, -310949822, -1653808685, 840726584});
   
   public static ItemStack makeHead(Text displayName, String name, String textureValue, UUID ownerId){
      return makeHead(displayName,name,textureValue,"",Uuids.toIntArray(ownerId));
   }
   
   public static ItemStack makeHead(Text displayName, String name, String textureValue, int[] ownerId){
      return makeHead(displayName,name,textureValue,"",ownerId);
   }
   
   public static ItemStack makeHead(Text displayName, String name, String textureValue, String signature, int[] ownerId){
      ItemStack headStack = new ItemStack(Items.PLAYER_HEAD);
      NbtCompound tag = headStack.getOrCreateNbt();
      NbtCompound skullOwner = new NbtCompound();
      NbtIntArray idList = new NbtIntArray(ownerId);
      NbtCompound properties = new NbtCompound();
      NbtList textureList = new NbtList();
      NbtCompound texture0 = new NbtCompound();
      
      texture0.putString("Value",textureValue);
      if(!signature.isEmpty()) texture0.putString("Signature",signature);
      textureList.add(texture0);
      properties.put("textures",textureList);
      
      skullOwner.put("Id",idList);
      skullOwner.put("Properties",properties);
      skullOwner.putString("Name",name);
      tag.put("SkullOwner",skullOwner);
      headStack.setNbt(tag);
      
      headStack.setCustomName(displayName);
      
      return headStack;
   }
   
   public static ItemStack getColoredSheepHead(DyeColor color){
      return switch(color){
         case WHITE -> WHITE_SHEEP;
         case ORANGE -> ORANGE_SHEEP;
         case MAGENTA -> MAGENTA_SHEEP;
         case LIGHT_BLUE -> LIGHT_BLUE_SHEEP;
         case YELLOW -> YELLOW_SHEEP;
         case LIME -> LIME_SHEEP;
         case PINK -> PINK_SHEEP;
         case GRAY -> GRAY_SHEEP;
         case LIGHT_GRAY -> LIGHT_GRAY_SHEEP;
         case CYAN -> CYAN_SHEEP;
         case PURPLE -> PURPLE_SHEEP;
         case BLUE -> BLUE_SHEEP;
         case BROWN -> BROWN_SHEEP;
         case GREEN -> GREEN_SHEEP;
         case RED -> RED_SHEEP;
         case BLACK -> BLACK_SHEEP;
      };
   }
   
   public static ItemStack getProfessionHead(String profession, boolean zombie){
      profession = profession.replace("minecraft:","");
      if(!zombie){
         return switch(profession){
            case "none" -> UNEMPLOYED_VILLAGER;
            case "armorer" -> ARMORER_VILLAGER;
            case "butcher" -> BUTCHER_VILLAGER;
            case "cartographer" -> CARTOGRAPHER_VILLAGER;
            case "cleric" -> CLERIC_VILLAGER;
            case "farmer" -> FARMER_VILLAGER;
            case "fisherman" -> FISHERMAN_VILLAGER;
            case "fletcher" -> FLETCHER_VILLAGER;
            case "leatherworker" -> LEATHERWORKER_VILLAGER;
            case "librarian" -> LIBRARIAN_VILLAGER;
            case "mason" -> MASON_VILLAGER;
            case "nitwit" -> NITWIT_VILLAGER;
            case "shepherd" -> SHEPHERD_VILLAGER;
            case "toolsmith" -> TOOLSMITH_VILLAGER;
            case "weaponsmith" -> WEAPONSMITH_VILLAGER;
            default -> UNEMPLOYED_VILLAGER;
         };
      }else{
         return switch(profession){
            case "none" -> ZOMBIE_VILLAGER;
            case "armorer" -> ZOMBIE_ARMORER;
            case "butcher" -> ZOMBIE_BUTCHER;
            case "cartographer" -> ZOMBIE_CARTOGRAPHER;
            case "cleric" -> ZOMBIE_CLERIC;
            case "farmer" -> ZOMBIE_FARMER;
            case "fisherman" -> ZOMBIE_FISHERMAN;
            case "fletcher" -> ZOMBIE_FLETCHER;
            case "leatherworker" -> ZOMBIE_LEATHERWORKER;
            case "librarian" -> ZOMBIE_LIBRARIAN;
            case "mason" -> ZOMBIE_MASON;
            case "nitwit" -> ZOMBIE_NITWIT;
            case "shepherd" -> ZOMBIE_SHEPHERD;
            case "toolsmith" -> ZOMBIE_TOOLSMITH;
            case "weaponsmith" -> ZOMBIE_WEAPONSMITH;
            default -> ZOMBIE_VILLAGER;
         };
      }
   }
}
