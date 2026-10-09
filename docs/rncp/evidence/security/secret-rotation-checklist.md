# Checklist — rotation des secrets potentiellement exposés

Date : 09/10/2026. Aucun secret ni ancienne valeur n'est affiché dans ce document. Le fichier `.env` frontend a été suivi dans l'historique (présence constatée dans un commit antérieur); ses valeurs éventuelles sont donc classées POTENTIELLEMENT COMPROMISES jusqu'à confirmation opérationnelle.

| Secret | État de rotation |
|---|---|
| JWT secret | À CONFIRMER |
| DB password | À CONFIRMER |
| SMTP secret | À CONFIRMER |
| Autres secrets éventuellement présents dans l'ancien `.env` | Inventaire par le porteur puis rotation à confirmer; aucune valeur consultée ou reproduite |

Le `.env` local est staged pour suppression et les patterns `.env` / `.env.*` sont ignorés, avec exception `.env.example`. Aucun historique n'a été réécrit. Ne pas pousser avant confirmation que toutes les anciennes valeurs réelles ont été révoquées/rotées. La rotation est une opération externe et n'est pas déclarée réalisée ici.
