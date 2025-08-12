<?php

namespace App\Enums;

enum ClothCategory: string
{
    // Accessories
    case Backpacks = 'Backpacks';
    case Belts = 'Belts';
    case Bracelet = 'Bracelet';
    case Caps = 'Caps';
    case Earrings = 'Earrings';
    case Gloves = 'Gloves';
    case Handbags = 'Handbags';
    case Hat = 'Hat';
    case Headband = 'Headband';
    case JewellerySet = 'JewellerySet';
    case Ring = 'Ring';
    case Scarves = 'Scarves';
    case Socks = 'Socks';
    case Sunglasses = 'Sunglasses';
    case Ties = 'Ties';
    case Umbrellas = 'Umbrellas';
    case Wallets = 'Wallets';
    case Watches = 'Watches';

    // Upper wear
    case Blazers = 'Blazers';
    case Jackets = 'Jackets';
    case RainJacket = 'Rain Jacket';
    case Shirts = 'Shirts';
    case Shrug = 'Shrug';
    case Sweaters = 'Sweaters';
    case Sweatshirts = 'Sweatshirts';
    case Tops = 'Tops';
    case Tunics = 'Tunics';
    case Tshirts = 'Tshirts';
    case Waistcoat = 'Waistcoat';

    // Lower wear
    case Capris = 'Capris';
    case Churidar = 'Churidar';
    case Jeans = 'Jeans';
    case Jeggings = 'Jeggings';
    case Leggings = 'Leggings';
    case LoungePants = 'Lounge Pants';
    case LoungeShorts = 'Lounge Shorts';
    case Shorts = 'Shorts';
    case Skirts = 'Skirts';
    case Tights = 'Tights';
    case TrackPants = 'Track Pants';
    case Trousers = 'Trousers';

    // Mixed
    case Dresses = 'Dresses';
    case Jumpsuit = 'Jumpsuit';
    case Tracksuits = 'Tracksuits';

    // Footwear
    case CasualShoes = 'Casual Shoes';
    case Flats = 'Flats';
    case FlipFlops = 'Flip Flops';
    case FormalShoes = 'Formal Shoes';
    case Heels = 'Heels';
    case Sandals = 'Sandals';
    case SportsSandals = 'Sports Sandals';
    case SportsShoes = 'Sports Shoes';

    case Topwear = 'Topwear';
    case Bottomwear = 'Bottomwear';
    case Accessories = 'Accessories';
    case Footwear = 'Footwear';

    case Casual = 'Casual';
    case Ethnic = 'Ethnic';
    case Formal = 'Formal';
    case Party = 'Party';
    case SmartCasual = 'Smart Casual';
    case Sports = 'Sports';
    case Travel = 'Travel';

    public function label(): string
    {
        return match($this) {
            self::Casual => 'Повседневный',
            self::Ethnic => 'Этнический',
            self::Formal => 'Формальный',
            self::Party => 'Вечерний',
            self::SmartCasual => 'Умный повседневный',
            self::Sports => 'Спортивный',
            self::Travel => 'Для путешествий',

            self::Topwear => 'Верхняя часть одежды',
            self::Bottomwear => 'Нижняя часть одежды',
            self::Accessories => 'Акссесуар',
            self::Footwear => 'Обувь',
            // Accessories
            self::Backpacks => 'Рюкзаки',
            self::Belts => 'Ремни',
            self::Bracelet => 'Браслеты',
            self::Caps => 'Шапки',
            self::Earrings => 'Серьги',
            self::Gloves => 'Перчатки',
            self::Handbags => 'Сумки',
            self::Hat => 'Шляпы',
            self::Headband => 'Ободки',
            self::JewellerySet => 'Комплекты украшений',
            self::Ring => 'Кольца',
            self::Scarves => 'Шарфы',
            self::Socks => 'Носки',
            self::Sunglasses => 'Солнцезащитные очки',
            self::Ties => 'Галстуки',
            self::Umbrellas => 'Зонты',
            self::Wallets => 'Кошельки',
            self::Watches => 'Часы',

            // Upper wear
            self::Blazers => 'Блейзеры',
            self::Jackets => 'Куртки',
            self::RainJacket => 'Дождевик',
            self::Shirts => 'Рубашки',
            self::Shrug => 'Накидка', // поправил перевод
            self::Sweaters => 'Свитера',
            self::Sweatshirts => 'Толстовки',
            self::Tops => 'Топы',
            self::Tunics => 'Туники',
            self::Tshirts => 'Футболки',
            self::Waistcoat => 'Жилет',

            // Lower wear
            self::Capris => 'Капри',
            self::Churidar => 'Чуридар',
            self::Jeans => 'Джинсы',
            self::Jeggings => 'Джеггинсы',
            self::Leggings => 'Леггинсы',
            self::LoungePants => 'Брюки для отдыха',
            self::LoungeShorts => 'Шорты для отдыха',
            self::Shorts => 'Шорты',
            self::Skirts => 'Юбки',
            self::Tights => 'Колготки',
            self::TrackPants => 'Спортивные брюки',
            self::Trousers => 'Брюки',

            // Mixed
            self::Dresses => 'Платья',
            self::Jumpsuit => 'Комбинезон',
            self::Tracksuits => 'Спортивные костюмы',

            // Footwear
            self::CasualShoes => 'Повседневная обувь',
            self::Flats => 'Ботинки',
            self::FlipFlops => 'Шлёпанцы',
            self::FormalShoes => 'Классическая обувь',
            self::Heels => 'Каблуки',
            self::Sandals => 'Сандалии',
            self::SportsSandals => 'Спортивные сандалии',
            self::SportsShoes => 'Спортивная обувь',
        };
    }

    public static function fromLabel(string $label): ?self
    {
        return match($label) {
            // Accessories
            'Рюкзаки' => self::Backpacks,
            'Ремни' => self::Belts,
            'Браслеты' => self::Bracelet,
            'Шапки' => self::Caps,
            'Серьги' => self::Earrings,
            'Перчатки' => self::Gloves,
            'Сумки' => self::Handbags,
            'Шляпы' => self::Hat,
            'Ободки' => self::Headband,
            'Комплекты украшений' => self::JewellerySet,
            'Кольца' => self::Ring,
            'Шарфы' => self::Scarves,
            'Носки' => self::Socks,
            'Солнцезащитные очки' => self::Sunglasses,
            'Галстуки' => self::Ties,
            'Зонты' => self::Umbrellas,
            'Кошельки' => self::Wallets,
            'Часы' => self::Watches,

            // Upper wear
            'Блейзеры' => self::Blazers,
            'Куртки' => self::Jackets,
            'Дождевик' => self::RainJacket,
            'Рубашки' => self::Shirts,
            'Накидка' => self::Shrug,
            'Свитера' => self::Sweaters,
            'Толстовки' => self::Sweatshirts,
            'Топы' => self::Tops,
            'Туники' => self::Tunics,
            'Футболки' => self::Tshirts,
            'Жилет' => self::Waistcoat,

            // Lower wear
            'Капри' => self::Capris,
            'Чуридар' => self::Churidar,
            'Джинсы' => self::Jeans,
            'Джеггинсы' => self::Jeggings,
            'Леггинсы' => self::Leggings,
            'Брюки для отдыха' => self::LoungePants,
            'Шорты для отдыха' => self::LoungeShorts,
            'Шорты' => self::Shorts,
            'Юбки' => self::Skirts,
            'Колготки' => self::Tights,
            'Спортивные брюки' => self::TrackPants,
            'Брюки' => self::Trousers,

            // Mixed
            'Платья' => self::Dresses,
            'Комбинезон' => self::Jumpsuit,
            'Спортивные костюмы' => self::Tracksuits,

            // Footwear
            'Повседневная обувь' => self::CasualShoes,
            'Ботинки' => self::Flats,
            'Шлёпанцы' => self::FlipFlops,
            'Классическая обувь' => self::FormalShoes,
            'Каблуки' => self::Heels,
            'Сандалии' => self::Sandals,
            'Спортивные сандалии' => self::SportsSandals,
            'Спортивная обувь' => self::SportsShoes,

            default => null,
        };
    }
}
