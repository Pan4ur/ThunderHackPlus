package com.github.lunatrius.schematica.reference;

@SuppressWarnings("HardCodedStringLiteral")
public final class Names {
    public static final class Config {
        public static final class Category {
            public static final String DEBUG = "debug";
            public static final String RENDER = "render";
            public static final String PRINTER = "printer";
            public static final String PRINTER_SWAPSLOTS = "printer.swapslots";
            public static final String GENERAL = "general";
            public static final String SERVER = "server";
        }
        public static final String DUMP_BLOCK_LIST = "dumpBlockList";
        public static final String DUMP_BLOCK_LIST_DESC = "Dump all block states on startup.";
        public static final String SHOW_DEBUG_INFO = "showDebugInfo";
        public static final String SHOW_DEBUG_INFO_DESC = "Display extra information on the debug screen (F3).";

        public static final String ALPHA_ENABLED = "alphaEnabled";
        public static final String ALPHA_ENABLED_DESC = "Enable transparent textures.";
        public static final String ALPHA = "alpha";
        public static final String ALPHA_DESC = "Alpha value used when rendering the schematic (1.0 = opaque, 0.5 = half transparent, 0.0 = transparent).";
        public static final String HIGHLIGHT = "highlight";
        public static final String HIGHLIGHT_DESC = "Highlight invalid placed blocks and to be placed blocks.";
        public static final String HIGHLIGHT_AIR = "highlightAir";
        public static final String HIGHLIGHT_AIR_DESC = "Highlight blocks that should be air.";
        public static final String BLOCK_DELTA = "blockDelta";
        public static final String BLOCK_DELTA_DESC = "Delta value used for highlighting (if you experience z-fighting increase this).";
        public static final String RENDER_DISTANCE = "renderDistance";
        public static final String RENDER_DISTANCE_DESC = "Schematic render distance.";

        public static final String PLACE_DELAY = "placeDelay";
        public static final String PLACE_DELAY_DESC = "Delay between placement attempts (in ticks).";
        public static final String TIMEOUT = "timeout";
        public static final String TIMEOUT_DESC = "Timeout before re-trying failed blocks.";
        public static final String PLACE_DISTANCE = "placeDistance";
        public static final String PLACE_DISTANCE_DESC = "Maximum placement distance.";
        public static final String PLACE_INSTANTLY = "placeInstantly";
        public static final String PLACE_INSTANTLY_DESC = "Place all blocks that can be placed in one tick.";
        public static final String DESTROY_BLOCKS = "destroyBlocks";
        public static final String DESTROY_BLOCKS_DESC = "The printer will destroy blocks (creative mode only).";
        public static final String DESTROY_INSTANTLY = "destroyInstantly";
        public static final String DESTROY_INSTANTLY_DESC = "Destroy all blocks that can be destroyed in one tick.";
        public static final String PLACE_ADJACENT = "placeAdjacent";
        public static final String PLACE_ADJACENT_DESC = "Place blocks only if there is an adjacent block next to them.";
        public static final String SWAP_SLOT = "swapSlot";
        public static final String SWAP_SLOT_DESC = "Allow the printer to use this hotbar slot.";

        public static final String SCHEMATIC_DIRECTORY = "schematicDirectory";
        public static final String SCHEMATIC_DIRECTORY_DESC = "Schematic directory.";
        public static final String EXTRA_AIR_BLOCKS = "extraAirBlocks";
        public static final String EXTRA_AIR_BLOCKS_DESC = "Extra blocks to consider as air for the schematic renderer.";
        public static final String SORT_TYPE = "sortType";
        public static final String SORT_TYPE_DESC = "Default sort type for the material list.";

        public static final String PRINTER_ENABLED = "printerEnabled";
        public static final String PRINTER_ENABLED_DESC = "Allow players to use the printer.";
        public static final String SAVE_ENABLED = "saveEnabled";
        public static final String SAVE_ENABLED_DESC = "Allow players to save schematics.";
        public static final String LOAD_ENABLED = "loadEnabled";
        public static final String LOAD_ENABLED_DESC = "Allow players to load schematics.";

        public static final String PLAYER_QUOTA_KILOBYTES = "playerQuotaKilobytes";
        public static final String PLAYER_QUOTA_KILOBYTES_DESC = "Amount of storage provided per-player for schematics on the server.";

        public static final String LANG_PREFIX = Reference.MODID + ".config";
    }

    public static final class Command {
        public static final class Save {
            public static final class Message {
                public static final String USAGE = "/schematicaSave <началоX> <началоY> <началоZ> <конецX> <конецY> <конецZ> <название> [формат]";
                public static final String PLAYERS_ONLY = "Только игроки могут использовать данную команду.";
                public static final String QUOTA_EXCEEDED = "Превышена квота сервера. Используйте /schematicaList и /schematicaRemove, чтобы удалить старые схемы.";
                public static final String PLAYER_SCHEMATIC_DIR_UNAVAILABLE = "Произошла ошибка на сервере, из-за которой неудалось сохранить схему. Свяжитесь с администратором сервера.";
            }

            public static final String NAME = "schematicaSave";
        }

        public static final class List {
            public static final class Message {
                public static final String USAGE = "/schematicaList [страница]";
                public static final String REMOVE = "Удалить";
                public static final String DOWNLOAD = "Загрузить";
                public static final String PAGE_HEADER = "--- Schemes, page %d из %d ---";
                public static final String NO_SUCH_PAGE = "Данной страницы не существует.";
                public static final String NO_SCHEMATICS = "Нет доступных схем.";
            }

            public static final String NAME = "schematicaList";
        }

        public static final class Remove {
            public static final class Message {
                public static final String USAGE = "/schematicaRemove <название>";
                public static final String PLAYERS_ONLY = "schematica.command.save.playersOnly";
                public static final String SCHEMATIC_REMOVED = "Scheme «%s» succefully deleted.";
                public static final String SCHEMATIC_NOT_FOUND = "Scheme «%s» not found.";
                public static final String ARE_YOU_SURE_START = "Уверены?";
                public static final String YES = "gui.yes";
            }

            public static final String NAME = "schematicaRemove";
        }

        public static final class Download {
            public static final class Message {
                public static final String USAGE = "/schematicaDownload <название>";
                public static final String PLAYERS_ONLY = "schematica.command.save.playersOnly";
                public static final String DOWNLOAD_STARTED = "Downloading «%s»...";
                public static final String DOWNLOAD_SUCCEEDED = "Scheme «%s» succesfully downloaded";
                public static final String DOWNLOAD_FAILED = "Произошла ошибка во время загрузки.";
            }

            public static final String NAME = "schematicaDownload";
        }

        public static final class Replace {
            public static final class Message {
                public static final String USAGE = "/schematicaReplace <оригинал> <замена>";
                public static final String NO_SCHEMATIC = "Нет загруженных схем.";
                public static final String SUCCESS = "%s blocks changed.";
            }

            public static final String NAME = "schematicaReplace";
        }
    }

    public static final class Messages {
        public static final String INVALID_BLOCK = "Invalid block «%s»";
        public static final String INVALID_PROPERTY = "Invalid property «%s»";
        public static final String INVALID_PROPERTY_FOR_BLOCK = "Invalid property «%s» for block «%s».";
    }

    public static final class Gui {
        public static final class Load {
            public static final String TITLE = "Выбор схемы";
            public static final String FOLDER_INFO = "(Поместите сюда схемы)";
            public static final String OPEN_FOLDER = "Папка со схемами";
            public static final String NO_SCHEMATIC = "-- Нет схем --";
        }

        public static final class Save {
            public static final String POINT_RED = "Красная точка";
            public static final String POINT_BLUE = "Синяя точка";
            public static final String SAVE = "Сохранить";
            public static final String SAVE_SELECTION = "Сохранение схемы";
            public static final String FORMAT = "Format: %s";
        }

        public static final class Control {
            public static final String MOVE_SCHEMATIC = "Перемещение схемы";
            public static final String MATERIALS = "Материалы";
            public static final String OPERATIONS = "Операции";

            public static final String UNLOAD = "Выгрузить";
            public static final String MODE_ALL = "Все слои";
            public static final String MODE_LAYERS = "Один слой";
            public static final String MODE_BELOW = "Все ниже";
            public static final String HIDE = "Скрыть";
            public static final String SHOW = "Показать";
            public static final String MOVE_HERE = "Сместить сюда";
            public static final String FLIP = "Отразить";
            public static final String ROTATE = "Повернуть";
            public static final String TRANSFORM_PREFIX = "schematica.gui.";

            public static final String MATERIAL_NAME = "Материал";
            public static final String MATERIAL_AMOUNT = "Количество";
            public static final String MATERIAL_AVAILABLE = "Доступен";
            public static final String MATERIAL_MISSING = "Отсутствует";

            public static final String SORT_PREFIX = "хз что это";
            public static final String DUMP = "Сохранить в файл";
        }

        public static final String X = "X";
        public static final String Y = "Y";
        public static final String Z = "Z";
        public static final String ON = "Вкл";
        public static final String OFF = "Выкл";
        public static final String DONE = "Готово";
    }

    public static final class Keys {
        public static final String CATEGORY = "Schematica";
        public static final String LOAD = "Загрузить схему";
        public static final String SAVE = "Сохранить схему";
        public static final String CONTROL = "Управление схемой";
        public static final String LAYER_INC = "Следующий слой";
        public static final String LAYER_DEC = "Предыдущий слой";
        public static final String LAYER_TOGGLE = "Все/один слой";
        public static final String RENDER_TOGGLE = "Отображение";
        public static final String MOVE_HERE = "Передвинуть сюда";
        public static final String PICK_BLOCK = "Выбор блока в схеме";
    }

    public static final class NBT {
        public static final String MATERIALS = "Materials";
        public static final String FORMAT_ALPHA = "Alpha";
        public static final String FORMAT_STRUCTURE = "Structure";
        public static final String ICON = "Icon";
        public static final String BLOCKS = "Blocks";
        public static final String DATA = "Data";
        public static final String ADD_BLOCKS = "AddBlocks";
        public static final String ADD_BLOCKS_SCHEMATICA = "Add";
        public static final String WIDTH = "Width";
        public static final String LENGTH = "Length";
        public static final String HEIGHT = "Height";
        public static final String MAPPING_SCHEMATICA = "SchematicaMapping";
        public static final String TILE_ENTITIES = "TileEntities";
        public static final String ENTITIES = "Entities";
        public static final String EXTENDED_METADATA = "ExtendedMetadata";
    }

    public static final class Formats {
        public static final String CLASSIC = "Classic";
        public static final String ALPHA = "Стандартный";
        public static final String STRUCTURE = "Блок-конструктор";
        public static final String INVALID = "Неизвестный";
    }

    public static final class Extensions {
        public static final String SCHEMATIC = ".schematic";
        public static final String STRUCTURE = ".nbt";
    }
}
