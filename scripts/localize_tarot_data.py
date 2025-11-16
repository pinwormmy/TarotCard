#!/usr/bin/env python3
"""Utility to replace tarot_data.json contents with Korean-localized strings."""
from __future__ import annotations

import json
from pathlib import Path

from tarot_translations import CARD_TRANSLATIONS

ROOT = Path(__file__).resolve().parents[1]
DATA_PATH = ROOT / "app" / "src" / "main" / "assets" / "tarot_data.json"

ARCANA_MAP = {
    "Major": "메이저 아르카나",
    "Minor": "마이너 아르카나",
    "대아르카나": "메이저 아르카나",
    "소아르카나": "마이너 아르카나",
    "메이저 아르카나": "메이저 아르카나",
    "마이너 아르카나": "마이너 아르카나",
}

SUIT_LABELS = {
    "wands": "완드",
    "cups": "컵",
    "swords": "소드",
    "pentacles": "펜타클",
}

RANK_LABELS = {
    "ace": "에이스",
    "2": "2",
    "3": "3",
    "4": "4",
    "5": "5",
    "6": "6",
    "7": "7",
    "8": "8",
    "9": "9",
    "10": "10",
    "page": "시종",
    "knight": "기사",
    "queen": "여왕",
    "king": "왕",
}

SUIT_DESCRIPTIONS = {
    "wands": "{}는 완드 슈트가 품은 창의성과 영감의 흐름을 담아냅니다.",
    "cups": "{}는 컵 슈트가 상징하는 감정과 관계의 물결을 전합니다.",
    "swords": "{}는 소드 슈트가 다루는 사고와 진실의 영역을 드러냅니다.",
    "pentacles": "{}는 펜타클 슈트가 맡는 물질적이고 실용적인 삶의 측면을 비춥니다.",
}

KEYWORD_MAP = {
    "absolution": "용서",
    "abundance": "풍요",
    "achievement": "성취",
    "action": "행동",
    "alchemy": "연금술",
    "alignment": "정렬",
    "assessment": "평가",
    "authority": "권위",
    "awakening": "각성",
    "awareness": "자각",
    "balance": "균형",
    "beauty": "아름다움",
    "beginnings": "시작",
    "bondage": "속박",
    "calling": "소명",
    "cause and effect": "인과",
    "challenge": "도전",
    "chance": "기회",
    "change": "변화",
    "choices": "선택",
    "clarity": "명료함",
    "closure": "마무리",
    "collapse": "붕괴",
    "comfort": "안락",
    "communication": "소통",
    "compassion": "연민",
    "completion": "완성",
    "control": "통제",
    "courage": "용기",
    "creativity": "창의성",
    "curiosity": "호기심",
    "cycles": "주기",
    "decision": "결정",
    "destiny": "운명",
    "dreams": "꿈",
    "drive": "추진력",
    "emotion": "감정",
    "expansion": "확장",
    "fairness": "공정성",
    "faith": "신뢰",
    "family": "가족",
    "fear": "두려움",
    "fertility": "다산",
    "fruition": "결실",
    "growth": "성장",
    "guidance": "인도",
    "harmony": "조화",
    "healing": "치유",
    "health": "건강",
    "hope": "희망",
    "influence": "영향력",
    "initiative": "주도성",
    "inner power": "내적 힘",
    "innocence": "순수함",
    "inspiration": "영감",
    "integration": "통합",
    "introspection": "내면 성찰",
    "intuition": "직관",
    "joy": "기쁨",
    "law": "법",
    "learning": "학습",
    "legacy": "유산",
    "logic": "논리",
    "love": "사랑",
    "manifestation": "현실화",
    "mastery": "숙련",
    "materialism": "물질주의",
    "maturity": "성숙",
    "messages": "메시지",
    "movement": "움직임",
    "mystery": "신비",
    "new beginnings": "새 출발",
    "nurture": "양육",
    "optimism": "낙관",
    "order": "질서",
    "passion": "열정",
    "patience": "인내",
    "pause": "멈춤",
    "perseverance": "끈기",
    "perspective": "관점",
    "potential": "잠재력",
    "power": "힘",
    "protection": "보호",
    "quest": "탐험",
    "rebirth": "재탄생",
    "relationships": "관계",
    "release": "해방",
    "resourcefulness": "재치",
    "rest": "휴식",
    "revelation": "계시",
    "ritual": "의식",
    "sacrifice": "희생",
    "self-worth": "자존감",
    "serenity": "평온",
    "shadow": "그림자",
    "skill": "기술",
    "solitude": "고독",
    "stability": "안정",
    "subconscious": "잠재의식",
    "success": "성공",
    "support": "지지",
    "teamwork": "팀워크",
    "temptation": "유혹",
    "tradition": "전통",
    "transformation": "변형",
    "travel": "여행",
    "truth": "진실",
    "turning point": "전환점",
    "union": "결합",
    "upheaval": "격변",
    "values": "가치",
    "vision": "비전",
    "warmth": "따뜻함",
    "willpower": "의지력",
    "wisdom": "지혜",
    "work": "일",
}


def infer_minor_name(card_id: str) -> str:
    suit_key, rank_key = card_id.split("_", 1)
    suit = SUIT_LABELS.get(suit_key)
    rank = RANK_LABELS.get(rank_key)
    if not suit or not rank:
        raise ValueError(f"Missing suit/rank mapping for {card_id}")
    return f"{suit} {rank}"


def infer_minor_description(card_name: str, suit_key: str) -> str:
    template = SUIT_DESCRIPTIONS.get(suit_key)
    if not template:
        raise ValueError(f"Missing suit description for {suit_key}")
    return template.format(card_name)


def translate_keywords(keywords: list[str]) -> list[str]:
    translated = []
    for word in keywords:
        mapped = KEYWORD_MAP.get(word)
        if mapped is not None:
            translated.append(mapped)
        elif not word.isascii():
            translated.append(word)
        else:
            raise KeyError(f"Missing keyword translation for '{word}'")
    return translated


def localize_card(card: dict) -> dict:
    card_id = card["id"]
    if card_id not in CARD_TRANSLATIONS:
        raise KeyError(f"Missing translation entry for {card_id}")
    overrides = CARD_TRANSLATIONS[card_id]
    upright = overrides.get("uprightMeaning", "").strip()
    reversed_text = overrides.get("reversedMeaning", "").strip()
    if not upright or not reversed_text:
        raise ValueError(f"Card {card_id} lacks meaning translations")

    arcana = ARCANA_MAP.get(card.get("arcana"), card.get("arcana"))
    name_override = overrides.get("name", "").strip()
    description_override = overrides.get("description", "").strip()

    if card_id.startswith(tuple(SUIT_LABELS.keys())):
        name = name_override or infer_minor_name(card_id)
        suit_key = card_id.split("_", 1)[0]
        description = description_override or infer_minor_description(name, suit_key)
    else:
        name = name_override or card.get("name")
        description = description_override or card.get("description")

    keywords = translate_keywords(card.get("keywords") or [])

    return {
        "id": card_id,
        "name": name,
        "arcana": arcana,
        "uprightMeaning": upright,
        "reversedMeaning": reversed_text,
        "description": description,
        "keywords": keywords,
        "imageUrl": card.get("imageUrl"),
    }


def main() -> None:
    data = json.loads(DATA_PATH.read_text(encoding="utf-8"))
    localized = [localize_card(card) for card in data]
    DATA_PATH.write_text(
        json.dumps(localized, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
