<template>
  <v-container class="py-6">
    <v-breadcrumbs
      :items="[{ title: 'Accueil', to: '/' }, { title: 'Élève' }]"
      class="mb-4"
    />
    <v-card class="mb-4">
      <v-card-text
        class="d-flex align-center justify-space-between flex-wrap"
        style="gap: 12px"
      >
        <div class="d-flex align-center" style="gap: 12px">
          <v-avatar :color="avatarColor" size="44">
            <span class="avatar-initials text-h6">{{ initials }}</span>
          </v-avatar>
          <div>
            <div class="text-h6">{{ fullName }}</div>
            <div class="text-medium-emphasis">{{ gender }}</div>
          </div>
        </div>
        <div class="d-flex align-center flex-wrap" style="gap: 8px">
          <v-chip
            v-if="details?.classeNom"
            variant="tonal"
            color="primary"
            size="small"
            >{{ details.classeNom }}</v-chip
          >
          <v-chip v-if="details?.ecoleNom" variant="tonal" size="small">{{
            details.ecoleNom
          }}</v-chip>
        </div>
      </v-card-text>
    </v-card>

    <v-card>
      <v-tabs v-model="tab" class="gradient-button" density="comfortable">
        <v-tab value="infos" class="text-white">Infos</v-tab>
        <v-tab value="notes" class="text-white">Notes</v-tab>
        <v-tab value="absences" class="text-white">Absences</v-tab>
        <v-tab value="remarques" class="text-white">Remarques</v-tab>
      </v-tabs>

      <v-card-text>
        <v-window v-model="tab">
          <!-- ========== INFOS ========== -->
          <v-window-item value="infos">
            <div v-if="loading.details">
              <v-skeleton-loader
                type="list-item-two-line, list-item-two-line, list-item-two-line"
              />
            </div>
            <div v-else>
              <v-row>
                <v-col cols="12" md="4">
                  <v-list density="compact">
                    <v-list-item
                      title="Nom"
                      :subtitle="details?.nom || 'N/C'"
                    />
                    <v-list-item
                      title="Prénom"
                      :subtitle="details?.prenom || 'N/C'"
                    />
                  </v-list>
                </v-col>
                <v-col cols="12" md="8">
                  <v-alert
                    v-if="!details?.classroom && !details?.school"
                    type="info"
                    variant="tonal"
                  >
                    Aucune inscription active (classe/école) pour cet élève.
                  </v-alert>
                  <div v-else class="text-medium-emphasis">
                    Actuellement en <b>{{ details?.classroom }}</b> —
                    {{ details?.school || "École N/C" }}
                  </div>
                </v-col>
              </v-row>
            </div>
          </v-window-item>

          <!-- ========== NOTES ========== -->
          <v-window-item value="notes">
            <!-- Stats cards en haut -->
            <v-row dense class="mb-4">
              <v-col cols="12" sm="4">
                <v-card variant="tonal" color="primary">
                  <v-card-text class="text-center">
                    <div class="text-h4 font-weight-bold">
                      {{ averageNote }}
                    </div>
                    <div class="text-caption">Moyenne générale</div>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="4">
                <v-card variant="tonal" color="success">
                  <v-card-text class="text-center">
                    <div class="text-h4 font-weight-bold">{{ bestNote }}</div>
                    <div class="text-caption">Meilleure note</div>
                  </v-card-text>
                </v-card>
              </v-col>
              <v-col cols="12" sm="4">
                <v-card variant="tonal" color="info">
                  <v-card-text class="text-center">
                    <div class="text-h4 font-weight-bold">
                      {{ notes.length }}
                    </div>
                    <div class="text-caption">Notes au total</div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>

            <!-- Mini graphique d'évolution -->
            <v-card v-if="notes.length > 0" class="mb-4" variant="outlined">
              <v-card-title class="text-subtitle-1"
                >Évolution des notes</v-card-title
              >
              <v-card-text>
                <div class="notes-chart">
                  <div
                    v-for="(note, index) in sortedNotesByDate"
                    :key="index"
                    class="note-bar-container"
                  >
                    <div
                      class="note-bar"
                      :style="{
                        height: `${(note.note / 20) * 100}%`,
                        backgroundColor: getNoteColor(note.note),
                      }"
                    >
                      <v-tooltip activator="parent" location="top">
                        {{ note.matiere }}: {{ note.note }}/20<br />
                        {{ fmtDate(note.date) }}
                      </v-tooltip>
                    </div>
                  </div>
                </div>
              </v-card-text>
            </v-card>

            <div class="d-flex align-center flex-wrap" style="gap: 8px">
              <v-text-field
                v-model="filters.matiere"
                label="Matière"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 180px"
              />
              <v-text-field
                v-model="displayNotesFrom"
                label="Du (jj/mm/aaaa)"
                placeholder="jj/mm/aaaa"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-text-field
                v-model="displayNotesTo"
                label="Au (jj/mm/aaaa)"
                placeholder="jj/mm/aaaa"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-btn color="primary" size="small" @click="loadNotes"
                >Filtrer</v-btn
              >
              <div class="ms-auto">
                <v-btn
                  size="small"
                  variant="tonal"
                  prepend-icon="mdi-note-plus"
                  @click="open.note = true"
                >
                  Ajouter une note
                </v-btn>
              </div>
            </div>

            <v-data-table
              class="mt-3"
              :loading="loading.notes"
              :items="notes"
              :headers="noteHeaders"
              :items-per-page="10"
              items-per-page-text="Nombre par page:"
            >
              <template #loading>
                <v-skeleton-loader type="table-row-divider@5" />
              </template>

              <template #[`item.date`]="{ value }">
                {{ fmtDate(value) }}
              </template>

              <template #[`item.note`]="{ value }">
                <b>{{ value }}</b>
              </template>

              <!-- Actions: delete icon -->
              <template #[`item.actions`]="{ item }">
                <v-btn
                  icon
                  size="small"
                  variant="text"
                  color="red"
                  @click="askDelete('note', item.id)"
                  :aria-label="`Supprimer la note ${item.id}`"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>

              <template #no-data>
                <div class="text-center py-8">
                  <v-icon size="64" color="grey-lighten-1" class="mb-4"
                    >mdi-note-text-outline</v-icon
                  >
                  <div class="text-h6 text-medium-emphasis mb-2">
                    Aucune note enregistrée
                  </div>
                  <div class="text-body-2 text-medium-emphasis mb-4">
                    Commencez par ajouter une première note pour cet élève
                  </div>
                  <v-btn
                    color="primary"
                    variant="tonal"
                    prepend-icon="mdi-note-plus"
                    @click="open.note = true"
                  >
                    Ajouter une note
                  </v-btn>
                </div>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- ========== ABSENCES ========== -->
          <v-window-item value="absences">
            <div class="d-flex align-center flex-wrap" style="gap: 8px">
              <v-text-field
                v-model="displayAbsencesFrom"
                label="Du (jj/mm/aaaa)"
                placeholder="jj/mm/aaaa"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-text-field
                v-model="displayAbsencesTo"
                label="Au (jj/mm/aaaa)"
                placeholder="jj/mm/aaaa"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
              />
              <v-select
                v-model="filters.absences.halfDay"
                :items="halfDayItems"
                item-title="label"
                item-value="code"
                label="Demi-journée"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 180px"
                clearable
              />
              <v-select
                v-model="filters.absences.justifie"
                :items="[
                  { text: 'Toutes', value: null },
                  { text: 'Oui', value: true },
                  { text: 'Non', value: false },
                ]"
                item-title="text"
                item-value="value"
                label="Justifiée"
                variant="outlined"
                density="compact"
                hide-details="auto"
                style="max-width: 160px"
                clearable
              />
              <v-btn color="primary" size="small" @click="loadAbsences"
                >Filtrer</v-btn
              >
              <div class="ms-auto">
                <v-btn
                  size="small"
                  variant="tonal"
                  prepend-icon="mdi-account-off"
                  @click="open.abs = true"
                  >Ajouter une absence</v-btn
                >
              </div>
            </div>

            <v-data-table
              class="mt-3"
              :loading="loading.absences"
              :items="absences"
              :headers="absHeaders"
              :items-per-page="10"
              items-per-page-text="Nombre par page:"
            >
              <template #loading>
                <v-skeleton-loader type="table-row-divider@5" />
              </template>

              <template #[`item.date`]="{ value }">
                {{ fmtDate(value) }}
              </template>

              <template #[`item.halfDay`]="{ item }">
                {{ item.halfDayLabel || djLabel(item.halfDayCode) }}
              </template>

              <template #[`item.justifie`]="{ value }">
                <v-chip
                  :color="value ? 'success' : 'warning'"
                  size="small"
                  variant="tonal"
                >
                  {{ value ? "Oui" : "Non" }}
                </v-chip>
              </template>

              <template #[`item.actions`]="{ item }">
                <v-btn
                  icon
                  size="small"
                  variant="text"
                  color="red"
                  @click="askDelete('absence', item.id)"
                  :aria-label="`Supprimer l'absence ${item.id}`"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>

              <template #no-data>
                <div class="text-center py-8">
                  <v-icon size="64" color="success" class="mb-4"
                    >mdi-check-circle-outline</v-icon
                  >
                  <div class="text-h6 text-medium-emphasis mb-2">
                    Aucune absence
                  </div>
                </div>
              </template>
            </v-data-table>
          </v-window-item>

          <!-- ========== REMARQUES ========== -->
          <v-window-item value="remarques">
            <div class="d-flex justify-end mb-2">
              <v-btn
                size="small"
                variant="tonal"
                prepend-icon="mdi-comment-plus"
                @click="open.rem = true"
              >
                Ajouter une remarque
              </v-btn>
            </div>
            <div v-if="loading.remarks">
              <v-skeleton-loader
                type="list-item-avatar-two-line, list-item-avatar-two-line, list-item-avatar-two-line"
              />
            </div>

            <v-list v-else-if="remarks.length" lines="two" class="remark-list">
              <v-list-item
                v-for="r in remarks"
                :key="r.id"
                :title="`${fmtDateTime(r.createdAt)} • ${remarkLabel(r.type)}`"
                :subtitle="`${
                  r.auteurLastName.toUpperCase() + ' ' + r.auteurFirstName ||
                  'Prof #' + r.authorId ||
                  'Auteur N/C'
                } — ${r.contenu}`"
                class="remark-item"
              >
                <template #prepend>
                  <v-avatar
                    size="40"
                    :style="remarkStyle(r.type)"
                    class="text-white"
                    >{{ remarkInitial(r.type) }}</v-avatar
                  >
                </template>
                <template #append>
                  <v-btn
                    icon
                    size="small"
                    variant="text"
                    color="red"
                    @click="askDelete('remark', r.id)"
                    :aria-label="`Supprimer la remarque ${r.id}`"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-list-item>
            </v-list>

            <div v-else class="text-center py-8">
              <v-icon size="64" color="grey-lighten-1" class="mb-4"
                >mdi-comment-outline</v-icon
              >
              <div class="text-h6 text-medium-emphasis mb-2">
                Aucune remarque
              </div>
              <div class="text-body-2 text-medium-emphasis mb-4">
                Ajoutez des remarques sur le comportement ou le travail de
                l'élève
              </div>
              <v-btn
                color="primary"
                variant="tonal"
                prepend-icon="mdi-comment-plus"
                @click="open.rem = true"
              >
                Ajouter une remarque
              </v-btn>
            </div>
          </v-window-item>
        </v-window>
      </v-card-text>
    </v-card>
    <v-card-actions class="justify-end"> </v-card-actions>

    <AddNoteDialog
      v-model="open.note"
      :pupil-id="pupilId"
      @saved="reloadNotes"
    />
    <AddAbsenceDialog
      v-model="open.abs"
      :pupil-id="pupilId"
      @saved="reloadAbsences"
    />
    <AddRemarkDialog
      v-model="open.rem"
      :pupil-id="pupilId"
      @saved="reloadRemarks"
    />

    <!-- Dialog de confirmation réutilisable -->
    <v-dialog v-model="confirm.show" max-width="430">
      <v-card>
        <v-card-title class="text-h6">{{ confirm.title }}</v-card-title>
        <v-card-text>{{ confirm.message }}</v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            variant="text"
            @click="closeConfirm"
            :disabled="confirm.loading"
          >
            Annuler
          </v-btn>
          <v-btn
            color="red"
            variant="flat"
            :loading="confirm.loading"
            @click="confirmDelete"
          >
            <v-icon start>mdi-delete</v-icon>
            Supprimer
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { api } from "../services/api";
import { useToastStore } from "../stores/toast";
import AddNoteDialog from "../components/dialogs/AddNoteDialog.vue";
import AddAbsenceDialog from "../components/dialogs/AddAbsenceDialog.vue";
import AddRemarkDialog from "../components/dialogs/AddRemarkDialog.vue";

const route = useRoute();
const toast = useToastStore();
const id = computed(() => route.params.id);
const gender = computed(() => {
  if (!details.value) return "N/C";
  const g = String(details.value.gender).toUpperCase();
  if (g === "GIRL") return "Fille";
  if (g === "BOY") return "Garçon";
  return "N/C";
});
const pupilId = computed(() => {
  const v = parseInt(route.params.id, 10);
  return isNaN(v) ? null : v;
});
const open = ref({ note: false, abs: false, rem: false });

// Etat du dialog de confirmation
const confirm = ref({
  show: false,
  loading: false,
  kind: /** @type {null | 'note' | 'absence' | 'remark'} */ (null),
  id: /** @type {null | number} */ (null),
  title: "",
  message: "",
});
function askDelete(kind, targetId) {
  confirm.value.kind = kind;
  confirm.value.id = targetId;
  confirm.value.title =
    kind === "note"
      ? "Supprimer cette note ?"
      : kind === "absence"
        ? "Supprimer cette absence ?"
        : "Supprimer cette remarque ?";
  confirm.value.message =
    "Cette action est définitive. Voulez-vous continuer ?";
  confirm.value.show = true;
}
function closeConfirm() {
  confirm.value.show = false;
  confirm.value.loading = false;
  confirm.value.kind = null;
  confirm.value.id = null;
}
async function confirmDelete() {
  if (!confirm.value.kind || !confirm.value.id) return;
  confirm.value.loading = true;
  try {
    if (confirm.value.kind === "note") {
      await api.delete(`/pupils/${id.value}/grades/${confirm.value.id}`);
      await loadNotes();
      toast.success("Note supprimée avec succès");
    } else if (confirm.value.kind === "absence") {
      await api.delete(`/pupils/${id.value}/absences/${confirm.value.id}`);
      await loadAbsences();
      toast.success("Absence supprimée avec succès");
    } else if (confirm.value.kind === "remark") {
      await api.delete(`/pupils/${id.value}/remarks/${confirm.value.id}`);
      await loadRemarks();
      toast.success("Remarque supprimée avec succès");
    }
    closeConfirm();
  } catch (e) {
    toast.error("La suppression a échoué.");
    confirm.value.loading = false;
  }
}

const tab = ref("infos");
const details = ref(null);
const notes = ref([]);
const absences = ref([]);
const remarks = ref([]);

const loading = ref({
  details: true,
  notes: false,
  absences: false,
  remarks: false,
});
const error = ref({ open: false, msg: "" });

const filters = ref({
  matiere: "",
  notes: { from: "", to: "" },
  absences: { from: "", to: "", halfDay: null, justifie: null },
});

// Helper functions for date conversion
function toDisplayDate(isoDate) {
  if (!isoDate) return "";
  const [y, m, d] = isoDate.split("-");
  return `${d}/${m}/${y}`;
}

function toISODate(displayDate) {
  if (!displayDate) return "";
  const parts = displayDate.split("/");
  if (parts.length !== 3) return "";
  const [d, m, y] = parts;
  return `${y}-${m.padStart(2, "0")}-${d.padStart(2, "0")}`;
}

// Computed properties for Notes date filters
const displayNotesFrom = computed({
  get: () => toDisplayDate(filters.value.notes.from),
  set: (val) => {
    filters.value.notes.from = toISODate(val);
  },
});

const displayNotesTo = computed({
  get: () => toDisplayDate(filters.value.notes.to),
  set: (val) => {
    filters.value.notes.to = toISODate(val);
  },
});

// Computed properties for Absences date filters
const displayAbsencesFrom = computed({
  get: () => toDisplayDate(filters.value.absences.from),
  set: (val) => {
    filters.value.absences.from = toISODate(val);
  },
});

const displayAbsencesTo = computed({
  get: () => toDisplayDate(filters.value.absences.to),
  set: (val) => {
    filters.value.absences.to = toISODate(val);
  },
});

const noteHeaders = [
  { title: "Date", key: "date" },
  { title: "Matière", key: "matiere" },
  { title: "Note", key: "note" },
  { title: "Commentaire", key: "commentaire" },
  { title: "", key: "actions", sortable: false, align: "end", width: 70 },
];
const absHeaders = [
  { title: "Date", key: "date" },
  { title: "Demi-journée", key: "halfDay" },
  { title: "Justifiée", key: "justifie" },
  { title: "Motif", key: "motif" },
  { title: "", key: "actions", sortable: false, align: "end", width: 70 },
];

const halfDayItems = [
  { code: 1, label: "Matin" },
  { code: 2, label: "Après-midi" },
  { code: 3, label: "Journée" },
];
function djLabel(code) {
  if (code === 1) return "Matin";
  if (code === 2) return "Après-midi";
  if (code === 3) return "Journée";
  return "N/C";
}

function remarkColor(type) {
  const t = String(type || "").toUpperCase();
  if (t === "INFO") return "remark-info";
  if (t === "WARNING") return "remark-warning";
  if (t === "CRITICAL") return "remark-critical";
  return "remark-info";
}

function remarkStyle(type) {
  const t = String(type || "").toUpperCase();
  if (t === "INFO")
    return {
      background: "linear-gradient(135deg, #3b82f6, #2563eb) !important",
    };
  if (t === "WARNING")
    return {
      background: "linear-gradient(135deg, #fb923c, #f97316) !important",
    };
  if (t === "CRITICAL")
    return {
      background: "linear-gradient(135deg, #f87171, #ef4444) !important",
    };
  return { background: "linear-gradient(135deg, #3b82f6, #2563eb) !important" };
}

function remarkLabel(type) {
  const t = String(type || "").toUpperCase();
  if (t === "INFO") return "Info";
  if (t === "WARNING") return "Avertissement";
  if (t === "CRITICAL") return "Important";
  return "Info";
}

function remarkInitial(type) {
  const t = String(type || "").toUpperCase();
  if (t === "INFO") return "I";
  if (t === "WARNING") return "A";
  if (t === "CRITICAL") return "!";
  return "I";
}

const fullName = computed(() =>
  details.value ? `${details.value.prenom} ${details.value.nom}` : "Élève",
);
const initials = computed(() => {
  if (!details.value) return "E";
  const a = (details.value.prenom || "").charAt(0) || "";
  const b = (details.value.nom || "").charAt(0) || "";
  return (a + b || "E").toUpperCase();
});

const avatarColor = computed(() => {
  if (!details.value) return "grey";
  const g = String(details.value.gender).toUpperCase();
  if (g === "GIRL") return "pink";
  if (g === "BOY") return "blue";
  return "grey";
});

// Stats pour les notes
const averageNote = computed(() => {
  if (!notes.value || notes.value.length === 0) return "N/C";
  const sum = notes.value.reduce((acc, n) => acc + (n.note || 0), 0);
  return (sum / notes.value.length).toFixed(2);
});

const bestNote = computed(() => {
  if (!notes.value || notes.value.length === 0) return "N/C";
  return Math.max(...notes.value.map((n) => n.note || 0));
});

const sortedNotesByDate = computed(() => {
  if (!notes.value) return [];
  return [...notes.value].sort((a, b) => new Date(a.date) - new Date(b.date));
});

function getNoteColor(note) {
  if (note >= 16) return "#10b981"; // vert
  if (note >= 12) return "#3b82f6"; // bleu
  if (note >= 10) return "#f59e0b"; // orange
  return "#ef4444"; // rouge
}

function reloadNotes() {
  loadNotes();
  toast.success("Note ajoutée avec succès");
}
function reloadAbsences() {
  loadAbsences();
  toast.success("Absence ajoutée avec succès");
}
function reloadRemarks() {
  loadRemarks();
  toast.success("Remarque ajoutée avec succès");
}

function fmtDate(d) {
  try {
    return new Date(d).toLocaleDateString("fr-FR");
  } catch {
    return d;
  }
}
function fmtDateTime(d) {
  try {
    return new Date(d).toLocaleString("fr-FR");
  } catch {
    return d;
  }
}

async function loadDetails() {
  loading.value.details = true;
  try {
    const { data } = await api.get(`/pupils/${id.value}`);
    details.value = data;
  } catch (e) {
    toast.error("Impossible de charger les infos élève.");
  } finally {
    loading.value.details = false;
  }
}

async function loadNotes() {
  loading.value.notes = true;
  try {
    const params = {};
    if (filters.value.matiere) params.matiere = filters.value.matiere;
    if (filters.value.notes.from) params.from = filters.value.notes.from;
    if (filters.value.notes.to) params.to = filters.value.notes.to;
    const { data } = await api.get(`/pupils/${id.value}/grades`, { params });
    notes.value = data;
  } catch (e) {
    toast.error("Chargement des notes impossible.");
  } finally {
    loading.value.notes = false;
  }
}

async function loadAbsences() {
  loading.value.absences = true;
  try {
    const f = filters.value.absences;
    const params = {};
    if (f.from) params.from = f.from;
    if (f.to) params.to = f.to;
    if (f.halfDay !== null && f.halfDay !== undefined)
      params.halfDay = f.halfDay;
    if (f.justifie !== null && f.justifie !== undefined)
      params.justifie = f.justifie;
    const { data } = await api.get(`/pupils/${id.value}/absences`, { params });
    absences.value = data;
  } catch (e) {
    toast.error("Chargement des absences impossible.");
  } finally {
    loading.value.absences = false;
  }
}

async function loadRemarks() {
  loading.value.remarks = true;
  try {
    const { data } = await api.get(`/pupils/${id.value}/remarks`);
    remarks.value = data;
  } catch (e) {
    toast.error("Chargement des remarques impossible.");
  } finally {
    loading.value.remarks = false;
  }
}

async function loadAll() {
  await loadDetails();
  await Promise.all([loadNotes(), loadAbsences(), loadRemarks()]);
}

onMounted(loadAll);
watch(() => id.value, loadAll);
</script>

<style scoped>
.gradient-button {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
  color: white !important;
}

/* Animations pour les transitions de tabs */
.v-window-item {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Animation pour les remarques */
.remark-list {
  animation: slideIn 0.4s ease-out;
}

.remark-item {
  transition: all 0.2s ease;
}

.remark-item:hover {
  background-color: rgba(0, 0, 0, 0.03);
  transform: translateX(4px);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Hover effect sur les boutons d'action */
.v-btn:hover {
  transform: scale(1.05);
  transition: transform 0.2s ease;
}

/* Graphique d'évolution des notes */
.notes-chart {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 120px;
  padding: 16px 0;
}

.note-bar-container {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: flex-end;
}

.note-bar {
  width: 100%;
  min-height: 4px;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.note-bar:hover {
  opacity: 0.8;
  transform: scaleY(1.05);
}

/* Skeleton loader pulse amélioré */
:deep(.v-skeleton-loader__bone) {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
