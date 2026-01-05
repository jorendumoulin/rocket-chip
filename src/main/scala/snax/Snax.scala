// See LICENSE.SiFive for license details.

package freechips.rocketchip.snax
import chisel3._
import org.chipsalliance.cde.config._
import org.chipsalliance.diplomacy.lazymodule._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.util.DontTouch
import freechips.rocketchip.devices.debug.Debug
import freechips.rocketchip.util.AsyncResetReg
import freechips.rocketchip.rocket.{WithNBigCores, WithNMedCores, WithNSmallCores, WithRV32, WithFP16, WithHypervisor, With1TinyCore, WithScratchpadsOnly, WithCloneRocketTiles, WithB}
import freechips.rocketchip.system.SimAXIMem

class WithJtagDTMSystem extends freechips.rocketchip.subsystem.WithJtagDTM
class WithDebugSBASystem extends freechips.rocketchip.subsystem.WithDebugSBA
class WithDebugAPB extends freechips.rocketchip.subsystem.WithDebugAPB


class SnaxConfig extends Config(
  new WithNBigCores(1) ++
  new WithDefaultMemPort ++
  new WithCoherentBusTopology ++
  //new WithDefaultMMIOPort ++
  //new WithDefaultSlavePort ++
  new WithTimebase(BigInt(1000000)) ++ // 1 MHz
  new WithDTS("freechips,rocketchip-unknown", Nil) ++
  new WithNExtTopInterrupts(2) ++
  new BaseSubsystemConfig
)

/** Example Top with periphery devices and ports, and a Rocket subsystem */
class SnaxSystem(implicit p: Parameters) extends RocketSubsystem
    // with HasAsyncExtInterrupts
    with CanHaveMasterAXI4MemPort
    // with CanHaveMasterAXI4MMIOPort
    // with CanHaveSlaveAXI4Port
{
  // optionally add ROM devices
  // Note that setting BootROMLocated will override the reset_vector for all tiles
  val bootRomParams = BootROMParams(
    address = 0x10000,
    size    = 0x10000,
    hang    = 0x10000
  )
  val bootROM  = p(BootROMLocated(location)).map { BootROM.attach(_, this, CBUS) }
  //val maskROMs = p(MaskROMLocated(location)).map { MaskROM.attach(_, this, CBUS) }

  override lazy val module = new ExampleRocketSystemModuleImp(this)
}

class ExampleRocketSystemModuleImp[+L <: SnaxSystem](_outer: L) extends RocketSubsystemModuleImp(_outer)
    with HasRTCModuleImp
    //with HasExtInterruptsModuleImp
    with DontTouch
