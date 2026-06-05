<template>
  <div class="monaco-root" :style="{ height }">
    <div ref="containerRef" class="monaco-container"></div>
  </div>
</template>

<script lang="ts" setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as monaco from 'monaco-editor'
import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
import JsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'
import CssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker'
import HtmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker'
import TsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'

type MonacoLanguage =
  | 'plaintext'
  | 'java'
  | 'javascript'
  | 'typescript'
  | 'json'
  | 'css'
  | 'scss'
  | 'less'
  | 'html'
  | 'sql'

const props = withDefaults(
  defineProps<{
    modelValue: string
    language?: MonacoLanguage
    theme?: 'vs' | 'vs-dark'
    readOnly?: boolean
    height?: string
    options?: monaco.editor.IStandaloneEditorConstructionOptions
  }>(),
  {
    language: 'plaintext',
    theme: 'vs',
    readOnly: false,
    height: '140px',
    options: undefined
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', v: string): void
  (e: 'ready', editor: monaco.editor.IStandaloneCodeEditor): void
}>()

const containerRef = ref<HTMLDivElement | null>(null)
let editor: monaco.editor.IStandaloneCodeEditor | null = null
let model: monaco.editor.ITextModel | null = null
let disposeChangeListener: monaco.IDisposable | null = null
let isApplying = false

;(self as any).MonacoEnvironment = {
  getWorker(_: unknown, label: string) {
    if (label === 'json') return new JsonWorker()
    if (label === 'css' || label === 'scss' || label === 'less') return new CssWorker()
    if (label === 'html' || label === 'handlebars' || label === 'razor') return new HtmlWorker()
    if (label === 'typescript' || label === 'javascript') return new TsWorker()
    return new EditorWorker()
  }
}

const setLanguage = (lang: MonacoLanguage) => {
  if (!model) return false
  monaco.editor.setModelLanguage(model, lang)
  return true
}

const setValue = (v: string, opts?: { preserveUndo?: boolean }) => {
  if (!editor || !model) return false
  const next = v ?? ''
  if (model.getValue() === next) return true

  isApplying = true
  try {
    if (opts?.preserveUndo) {
      editor.executeEdits('modelUpdate', [
        {
          range: model.getFullModelRange(),
          text: next
        }
      ])
      editor.pushUndoStop()
    } else {
      model.setValue(next)
    }
    editor.setScrollPosition({ scrollLeft: 0, scrollTop: 0 })
    return true
  } finally {
    isApplying = false
  }
}

const getValue = () => {
  return model?.getValue() ?? ''
}

const setReadOnly = (ro: boolean) => {
  editor?.updateOptions({ readOnly: ro })
}

const setTheme = (t: 'vs' | 'vs-dark') => {
  monaco.editor.setTheme(t)
}

const updateOptions = (opts: monaco.editor.IStandaloneEditorConstructionOptions) => {
  editor?.updateOptions(opts)
}

const layout = () => {
  editor?.layout()
}

const focus = () => {
  editor?.focus()
}

onMounted(() => {
  if (!containerRef.value) return

  model = monaco.editor.createModel(props.modelValue ?? '', props.language)
  editor = monaco.editor.create(containerRef.value, {
    model,
    theme: props.theme,
    readOnly: props.readOnly,
    automaticLayout: true,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    renderLineHighlight: 'none',
    folding: false,
    overviewRulerBorder: false,
    scrollbar: {
      verticalScrollbarSize: 6,
      horizontalScrollbarSize: 6
    },
    ...props.options
  })

  disposeChangeListener = editor.onDidChangeModelContent(() => {
    if (!model) return
    if (isApplying) return
    emit('update:modelValue', model.getValue())
  })

  emit('ready', editor)
})

onBeforeUnmount(() => {
  disposeChangeListener?.dispose()
  disposeChangeListener = null
  editor?.dispose()
  editor = null
  model?.dispose()
  model = null
})

defineExpose({
  setValue,
  getValue,
  setLanguage,
  setReadOnly,
  setTheme,
  updateOptions,
  layout,
  focus
})
</script>

<style scoped>
.monaco-root {
  width: 100%;
  border-radius: 14px;
  overflow: hidden;
  text-align: left;
}

.monaco-container {
  width: 100%;
  height: 100%;
}

:deep(.monaco-editor),
:deep(.monaco-editor .view-lines),
:deep(.monaco-editor .view-line) {
  text-align: left;
}
</style>
