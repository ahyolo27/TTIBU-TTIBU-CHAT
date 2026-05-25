import { useRouterState } from "@tanstack/react-router";
import { useEffect, useMemo, useRef, useState } from "react";
import { createPortal } from "react-dom";
import * as S from "./ModalShell.styles";
import { ChatContent } from "./contents/ChatContent";
import { SearchContent } from "./contents/SearchContent";
import { GroupContent } from "./contents/GroupContent";
import { useModels } from "@/hooks/useModels"; // ✅ 그대로 사용

const TYPE_ORDER = { layers: 0, search: 1, chat: 2 };
const ANIM_MS = 280;

export default function ModalShell({
  open,
  onOpen,
  onClose,
  type = "chat",
  setType,
  title = "브랜치",
  messages = [],
  input = "",
  onInputChange,
  onSend,
  peek = false,
  setPeek,
  showDock = true,
  onPick,

  // ✅ 모델 제어용
  modelUid,
  onModelChange,
  modelSource = "available",

  // ✅ 브랜치 연동용 (ChatFlowPage와 싱크)
  branchItems = [], // [{ label, value, active }]
  activeBranchKey = "전체", // "전체" 또는 branch_id 문자열
  onBranchSelect, // (value: string) => void

    // 🔥 추가: 어떤 chat_id를 중앙에 스크롤할지
  focusChatId,
}) {
  const panelRef = useRef(null);

  const [peekState, setPeekState] = useState(!!peek);
  useEffect(() => setPeekState(!!peek), [peek]);

  const routerState = useRouterState();
  const pathname = routerState.location.pathname;
  const hideChatDock = pathname.startsWith("/groups");

  // ✅ 브랜치 드롭다운 open 상태만 내부에서 관리
  const [branchOpen, setBranchOpen] = useState(false);

  // 서버 모델 불러오기 (라벨=modelName / 값=modelUid)
  const {
    dropdownItems = [], // [{ label, value(modelUid), modelCode, isDefault }]
    defaultModelUid = null,
    modelsLoading = false,
    modelsError = null,
  } = useModels({ source: modelSource }) ?? {};

  // 내부/외부 겸용 선택 상태 (외부에서 modelUid를 주면 그 값을 우선)
  const [innerModelUid, setInnerModelUid] = useState(null);

  useEffect(() => {
    if (modelUid && modelUid !== innerModelUid) {
      setInnerModelUid(modelUid);
    }
  }, [modelUid, innerModelUid]);

  useEffect(() => {
    if (!innerModelUid && !modelUid) {
      const fallback = dropdownItems[0]?.value ?? null;
      const next = defaultModelUid || fallback;
      if (next) {
        setInnerModelUid(next);
        onModelChange?.(next);
      }
    }
  }, [defaultModelUid, dropdownItems, innerModelUid, modelUid, onModelChange]);

  // ✅ 모델 드롭다운 열림 상태
  const [modelOpen, setModelOpen] = useState(false);

  useEffect(() => {
    if (open) panelRef.current?.focus();
  }, [open]);

  const portalRoot = useMemo(() => {
    if (typeof window === "undefined") return null;
    let el = document.getElementById("portal-root");
    if (!el) {
      el = document.createElement("div");
      el.id = "portal-root";
      document.body.appendChild(el);
    }
    return el;
  }, []);
  if (!portalRoot) return null;

  const stop = (e) => e.stopPropagation();

  const prevTypeRef = useRef(type);
  const [dir, setDir] = useState("forward");
  const [leavingType, setLeavingType] = useState(null);
  const [leavingHeader, setLeavingHeader] = useState(null);

  const [fullscreen, setFullscreen] = useState(false);

  useEffect(() => {
    const prev = prevTypeRef.current;
    if (prev !== type) {
      const prevIdx = TYPE_ORDER[prev] ?? 0;
      const nextIdx = TYPE_ORDER[type] ?? 0;
      setDir(nextIdx > prevIdx ? "forward" : "backward");
      setLeavingType(prev);
      setLeavingHeader(prev);
      const t = setTimeout(() => {
        setLeavingType(null);
        setLeavingHeader(null);
      }, ANIM_MS);
      prevTypeRef.current = type;
      return () => clearTimeout(t);
    }
  }, [type]);

  /* ===== Dock 공통 토글 ===== */
  const handleDockToggle = (nextType) => {
    if (open && type === nextType) {
      if (peekState) {
        setPeek?.(false);
        setPeekState(false);
        setFullscreen(false);
        onOpen?.();
        return;
      }
      onClose?.();
      return;
    }
    setType?.(nextType);
    setPeek?.(false);
    setPeekState(false);
    setFullscreen(false);
    if (!open) onOpen?.();
  };

  // ✅ 모델 드롭다운 라벨 계산
  const selectedModelLabel = (() => {
    const selectedUid = modelUid || innerModelUid || null;
    const found = (dropdownItems || []).find((i) => i.value === selectedUid);
    if (found) return found.label;
    if (modelsLoading) return "모델 불러오는 중…";
    if (modelsError) return "모델 로드 실패";
    return "모델 선택";
  })();

  // ✅ 모델 선택 핸들러
  const pickModel = (uid) => {
    if (!uid) return;
    onModelChange?.(uid);
    setInnerModelUid(uid);
    setModelOpen(false);
  };

  // ✅ 현재 브랜치 라벨 (ChatFlowPage에서 넘어온 branchItems 기준)
  const branchLabel = useMemo(() => {
    if (branchItems && branchItems.length > 0) {
      const found = branchItems.find((b) => b.value === activeBranchKey);
      if (found) return found.label;
    }
    return title; // fallback
  }, [branchItems, activeBranchKey, title]);

  /* ===== Header 렌더 ===== */
  const renderHeaderSlots = (renderType) => {
    if (renderType === "chat") {
      return (
        <>
          <S.HeaderLeft>
            <S.IconButton
              onClick={(e) => {
                e.stopPropagation();
                if (!open) onOpen?.();
                setPeek?.(false);
                setPeekState(false);
                setFullscreen((v) => !v);
              }}
              title={fullscreen ? "기본 너비로" : "전체 화면으로"}
              aria-label={fullscreen ? "기본 너비로" : "전체 화면으로"}
            >
              <i
                className={
                  fullscreen ? "fa-solid fa-compress" : "fa-solid fa-expand"
                }
              />
            </S.IconButton>
          </S.HeaderLeft>

          <S.HeaderCenter>
            <S.Dropdown>
              <S.DropdownToggler
                onClick={(e) => {
                  e.stopPropagation();
                  setBranchOpen((v) => !v);
                  setModelOpen(false);
                }}
              >
                <S.TogglerText>{branchLabel}</S.TogglerText>
              </S.DropdownToggler>

              {branchOpen && (
                <S.DropdownList onClick={stop}>
                  {(branchItems || []).map((b) => (
                    <S.DropdownItem
                      key={b.value}
                      $active={b.value === activeBranchKey}
                      onClick={(e) => {
                        e.stopPropagation();
                        onBranchSelect?.(b.value); // 🔥 ChatFlowPage 상태 변경
                        setBranchOpen(false);
                      }}
                    >
                      {b.label} {b.value === activeBranchKey && <span>✔</span>}
                    </S.DropdownItem>
                  ))}
                </S.DropdownList>
              )}
            </S.Dropdown>
          </S.HeaderCenter>

          <S.HeaderRight>
            <S.Dropdown>
              <S.DropdownToggler
                onClick={(e) => {
                  e.stopPropagation();
                  setModelOpen((v) => !v);
                  setBranchOpen(false);
                }}
              >
                <S.TogglerTextMuted>{selectedModelLabel}</S.TogglerTextMuted>
              </S.DropdownToggler>

              {modelOpen && (
                <S.DropdownList $right onClick={stop}>
                  {modelsLoading && (
                    <S.DropdownItem $active={false} disabled>
                      불러오는 중…
                    </S.DropdownItem>
                  )}
                  {!modelsLoading && modelsError && (
                    <S.DropdownItem $active={false} disabled>
                      모델 목록을 불러오지 못했습니다
                    </S.DropdownItem>
                  )}
                  {!modelsLoading &&
                    !modelsError &&
                    (dropdownItems || []).map((m) => (
                      <S.DropdownItem
                        key={m.value}
                        $active={(modelUid || innerModelUid) === m.value}
                        onClick={(e) => {
                          e.stopPropagation();
                          pickModel(m.value);
                        }}
                      >
                        {m.label}{" "}
                        {(modelUid || innerModelUid) === m.value && (
                          <span>✔</span>
                        )}
                      </S.DropdownItem>
                    ))}
                </S.DropdownList>
              )}
            </S.Dropdown>
          </S.HeaderRight>
        </>
      );
    }

    // search / layers 공통
    return (
      <>
        <S.HeaderLeft>
          <S.IconButton onClick={onClose} title="닫기" aria-label="닫기">
            <i className="fa-solid fa-angle-right" />
          </S.IconButton>
        </S.HeaderLeft>

        <S.HeaderCenter>
          <S.SearchTitle>
            {renderType === "search" ? "검색" : "그룹"}
          </S.SearchTitle>
        </S.HeaderCenter>

        <S.HeaderRight />
      </>
    );
  };

  const renderContentByType = (renderType) => {
    if (renderType === "chat") {
      return (
        <ChatContent
          messages={messages}
          input={input}
          onInputChange={onInputChange}
          onSend={onSend}

          focusChatId={focusChatId}
        />
      );
    }
    if (renderType === "search") return <SearchContent onPick={onPick} />;
    return <GroupContent onPick={onPick} />;
  };

  return createPortal(
    <S.Overlay $dim={fullscreen}>
      <S.Panel
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-expanded={open}
        tabIndex={-1}
        $open={open}
        $peek={peekState}
        $fullscreen={fullscreen}
        onClick={stop}
      >
        {showDock && (
          <S.Dock $fullscreen={fullscreen}>
            {!hideChatDock && (
              <S.DockButton
                title="그룹"
                onClick={() => handleDockToggle("layers")}
              >
                <i className="fa-solid fa-layer-group" />
              </S.DockButton>
            )}
            <S.DockButton
              title="검색"
              onClick={() => handleDockToggle("search")}
            >
              <i className="fa-solid fa-diagram-project" />
            </S.DockButton>
            {!hideChatDock && (
              <S.DockButton
                title="채팅"
                onClick={() => handleDockToggle("chat")}
              >
                <i className="fa-solid fa-comments" />
              </S.DockButton>
            )}
          </S.Dock>
        )}

        <S.Header>
          {leavingHeader && (
            <S.HeaderLayer
              $phase="leave"
              $dir={dir}
              key={`header-leave-${leavingHeader}`}
            >
              {renderHeaderSlots(leavingHeader)}
            </S.HeaderLayer>
          )}
          <S.HeaderLayer $phase="enter" $dir={dir} key={`header-enter-${type}`}>
            {renderHeaderSlots(type)}
          </S.HeaderLayer>
        </S.Header>

        <S.Body>
          {leavingType && (
            <S.ContentLayer
              $phase="leave"
              $dir={dir}
              key={`content-leave-${leavingType}`}
            >
              {renderContentByType(leavingType)}
            </S.ContentLayer>
          )}
          <S.ContentLayer
            $phase="enter"
            $dir={dir}
            key={`content-enter-${type}`}
          >
            {renderContentByType(type)}
          </S.ContentLayer>
        </S.Body>
      </S.Panel>
    </S.Overlay>,
    portalRoot
  );
}
