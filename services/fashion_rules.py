from typing import Dict, Tuple
import json


class FashionRules:
    def __init__(self):
        self.style_pairs = self._initialize_style_pairs()
        self.fashion_conflicts = {}

    def _initialize_style_pairs(self) -> Dict[Tuple[str, str], float]:
        styles = ["Casual", "Ethnic", "Formal", "Party", "Smart Casual", "Sports", "Travel"]

        base_pairs = {
            ("Casual", "Smart Casual"): 0.9,
            ("Smart Casual", "Casual"): 0.9,
            ("Casual", "Sports"): 0.5,
            ("Sports", "Casual"): 0.5,
            ("Casual", "Travel"): 0.5,
            ("Travel", "Casual"): 0.5,
            ("Casual", "Ethnic"): 0.4,
            ("Ethnic", "Casual"): 0.4,
            ("Casual", "Formal"): 0.6,
            ("Formal", "Casual"): 0.6,
            ("Casual", "Party"): 0.3,
            ("Party", "Casual"): 0.3,
            ("Smart Casual", "Formal"): 0.8,
            ("Formal", "Smart Casual"): 0.8,
            ("Smart Casual", "Party"): 0.5,
            ("Party", "Smart Casual"): 0.5,
            ("Smart Casual", "Sports"): 0.4,
            ("Sports", "Smart Casual"): 0.4,
            ("Smart Casual", "Travel"): 0.4,
            ("Travel", "Smart Casual"): 0.4,
            ("Smart Casual", "Ethnic"): 0.5,
            ("Ethnic", "Smart Casual"): 0.5,
            ("Formal", "Party"): 0.8,
            ("Party", "Formal"): 0.8,
            ("Formal", "Sports"): 0.3,
            ("Sports", "Formal"): 0.3,
            ("Formal", "Travel"): 0.3,
            ("Travel", "Formal"): 0.3,
            ("Formal", "Ethnic"): 0.3,
            ("Ethnic", "Formal"): 0.3,
            ("Party", "Sports"): 0.4,
            ("Sports", "Party"): 0.4,
            ("Party", "Travel"): 0.5,
            ("Travel", "Party"): 0.5,
            ("Party", "Ethnic"): 0.4,
            ("Ethnic", "Party"): 0.4,
            ("Sports", "Travel"): 0.7,
            ("Travel", "Sports"): 0.7,
            ("Sports", "Ethnic"): 0.3,
            ("Ethnic", "Sports"): 0.3,
            ("Travel", "Ethnic"): 0.5,
            ("Ethnic", "Travel"): 0.5,
            ("Casual", "Casual"): 1.0,
            ("Smart Casual", "Smart Casual"): 1.0,
            ("Formal", "Formal"): 1.0,
            ("Party", "Party"): 1.0,
            ("Sports", "Sports"): 1.0,
            ("Travel", "Travel"): 1.0,
            ("Ethnic", "Ethnic"): 1.0
        }

        style_matrix = {}
        for a in styles:
            for b in styles:
                style_matrix[(a, b)] = base_pairs.get((a, b), base_pairs.get((b, a), 1.0 if a == b else 0.5))

        return style_matrix

    def load_conflicts(self, file_path: str):
        with open(file_path, "r", encoding="utf-8") as f:
            data = json.load(f)

        for acc, other, score in data["hard_conflicts"]:
            self._add_conflict(acc, other, score)

        for rule in data["rules"]:
            self._process_rule(rule)

    def _add_conflict(self, item1: str, item2: str, score: float):
        self.fashion_conflicts[(item1, item2)] = self.fashion_conflicts[(item2, item1)] = score

    def _process_rule(self, rule: Dict):
        rule_type = rule["type"]

        if rule_type == "accessory_shoe":
            for shoe in rule["shoes"]:
                score = rule.get("override", {}).get(shoe, rule["score"])
                self._add_conflict(rule["accessory"], shoe, score)

        elif rule_type == "top_shoe":
            for top in rule["tops"]:
                for shoe in rule["shoes"]:
                    self._add_conflict(top, shoe, rule["score"])

        elif rule_type == "bottom_shoe":
            for bottom in rule["bottoms"]:
                for shoe in rule["shoes"]:
                    self._add_conflict(bottom, shoe, rule["score"])

        elif rule_type == "accessory_top":
            for top in rule["tops"]:
                self._add_conflict(rule["accessory"], top, rule["score"])

        elif rule_type == "accessory_bottom":
            for bottom in rule["bottoms"]:
                self._add_conflict(rule["accessory"], bottom, rule["score"])
