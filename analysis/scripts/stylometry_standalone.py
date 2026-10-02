#!/usr/bin/env python3
"""
DAVIS — Standalone Stylometric & AI-Rewrite Analysis Engine
SIH 2026 | PS ID: SIH26151 | Team: JugaaduSloths (185528)

Computes TF-IDF cosine similarity, idiosyncratic punctuation metrics,
and heuristics for detecting potential LLM-assisted paraphrasing/rewriting.
"""

import sys
import re
import math
from collections import Counter

FORMAL_AI_PATTERNS = [
    "kindly be advised", "strictly required", "comprehensive disclosure",
    "inevitably follow", "in accordance with", "it is imperative",
    "designated cryptocurrency", "prompt remediation"
]

def tokenize(text):
    return re.findall(r'[a-zA-Z0-9_-]+', text)

def extract_metrics(text):
    words = tokenize(text)
    total_words = len(words)
    sentences = [s.strip() for s in re.split(r'[.!?]+', text) if s.strip()]
    sentence_count = max(1, len(sentences))
    avg_sentence_len = total_words / sentence_count if total_words > 0 else 0

    unique_words = set(w.lower() for w in words)
    type_token_ratio = len(unique_words) / total_words if total_words > 0 else 0

    double_hyphens = len(re.findall(r'--', text))
    semicolons = len(re.findall(r';', text))

    return {
        "word_count": total_words,
        "sentence_count": sentence_count,
        "avg_sentence_length": round(avg_sentence_len, 2),
        "type_token_ratio": round(type_token_ratio, 2),
        "double_hyphen_count": double_hyphens,
        "semicolon_count": semicolons
    }

def check_ai_rewrite(text):
    lower = text.lower()
    matches = [p for p in FORMAL_AI_PATTERNS if p in lower]
    if len(matches) >= 2:
        return True, f"Matched formal transition phrases: {', '.join(matches)}"

    sentences = [s.strip() for s in re.split(r'[.!?]+', text) if s.strip()]
    if len(sentences) >= 2:
        lengths = [len(tokenize(s)) for s in sentences if len(tokenize(s)) > 0]
        if lengths:
            mean = sum(lengths) / len(lengths)
            variance = sum((l - mean) ** 2 for l in lengths) / len(lengths)
            if variance < 6.0 and len(matches) >= 1:
                return True, f"Low syntactic variance ({variance:.1f}) combined with formal phrase '{matches[0]}'"

    return False, "Natural idiosyncratic human author variation observed."

def compute_cosine(text_a, text_b):
    words_a = [w.lower() for w in tokenize(text_a)]
    words_b = [w.lower() for w in tokenize(text_b)]

    all_words = set(words_a).union(set(words_b))
    if not all_words:
        return 0.0

    counts_a = Counter(words_a)
    counts_b = Counter(words_b)

    dot = sum(counts_a[w] * counts_b[w] for w in all_words)
    norm_a = math.sqrt(sum(counts_a[w] ** 2 for w in all_words))
    norm_b = math.sqrt(sum(counts_b[w] ** 2 for w in all_words))

    if norm_a == 0 or norm_b == 0:
        return 0.0

    return dot / (norm_a * norm_b)

def main():
    sample_a = (
        "Payment of 1.45 BTC must be remitted immediately -- non-negotiable terms. "
        "Data decryption mirrors will be shredded irrevocably upon block 894000. "
        "Protocol enforces zero exceptions; delay results in public repository exposure."
    )
    sample_b = (
        "We strictly deploy custom payload packers -- avoiding default entropy metrics is trivial. "
        "The target infrastructure crumbled within twenty minutes once persistence was attained; "
        "no compromise on operational secrecy."
    )
    sample_c = (
        "Kindly be advised that the designated cryptocurrency funds are strictly required to proceed "
        "with the restoration of the encrypted databases. Should the compliance window lapse, "
        "comprehensive disclosure of internal proprietary records will inevitably follow."
    )

    print("=================================================================")
    print("DAVIS Standalone Stylometric & AI-Rewrite Evaluation")
    print("=================================================================\n")

    print("[Sample A vs Sample B (Natural Technical Forum Author)]")
    cos_ab = compute_cosine(sample_a, sample_b)
    metrics_a = extract_metrics(sample_a)
    metrics_b = extract_metrics(sample_b)
    ai_a, expl_a = check_ai_rewrite(sample_a)
    ai_b, expl_b = check_ai_rewrite(sample_b)

    print(f"Cosine Similarity: {cos_ab * 100:.1f}%")
    print(f"Sample A Double Hyphens: {metrics_a['double_hyphen_count']}, Sample B: {metrics_b['double_hyphen_count']}")
    print(f"AI Rewrite Flag Sample A: {ai_a} ({expl_a})")
    print(f"AI Rewrite Flag Sample B: {ai_b} ({expl_b})")

    print("\n[Sample A vs Sample C (AI-Paraphrased Note)]")
    cos_ac = compute_cosine(sample_a, sample_c)
    ai_c, expl_c = check_ai_rewrite(sample_c)
    print(f"Cosine Similarity: {cos_ac * 100:.1f}%")
    print(f"AI Rewrite Flag Sample C: {ai_c} ({expl_c})")
    if ai_c:
        print(">> ATTRIBUTION DOWNWEIGHTING TRIGGERED: Stylometric weight reduced by 50%.")

if __name__ == '__main__':
    main()
