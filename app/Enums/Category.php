<?php

namespace App\Enums;

enum Category: string
{
    case PUBLICATION = "PUBLICATION";
    case PATENT = "PATENT";
    case ARTICLE = "ARTICLE";
    case JOURNAL_ARTICLE = "JOURNAL_ARTICLE";
    case BOOK_ARTICLE = "BOOK_ARTICLE";
    case BOOK = "BOOK";
    case JOURNAL = "JOURNAL";

    public function toString() : string {
        return match($this) {
            self::PUBLICATION => 'publication',
            self::PATENT => 'patent',
            self::ARTICLE => 'article',
            self::JOURNAL_ARTICLE => 'article',
            self::BOOK_ARTICLE => 'article',
            self::BOOK => 'book',
            self::JOURNAL => 'journal',
        };
    }
    public function translate() : string {
        return match($this) {
            self::PUBLICATION => 'Публикация',
            self::PATENT => 'Патент',
            self::ARTICLE => 'Статья',
            self::JOURNAL_ARTICLE => 'Статья в журнале',
            self::BOOK_ARTICLE => 'Статья в сборнике',
            self::BOOK => 'Книга',
            self::JOURNAL => 'Журнал',
        };
    }
    public function translateToReport() :string {
        return match($this) {
            self::PUBLICATION => 'Публикации',
            self::PATENT => 'Патенты',
            self::ARTICLE => 'Статьи',
            self::JOURNAL_ARTICLE => 'Статьи в журналах',
            self::BOOK_ARTICLE => 'Статьи в сборниках',
            self::BOOK => 'Книги',
            self::JOURNAL => 'Журналы',
        };
    }
}

